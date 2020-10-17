package org.k2.resource.binary;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.lang3.RandomStringUtils;
import org.k2.resource.KeyGenerator;
import org.k2.resource.Resource;
import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;

import lombok.Getter;
import lombok.Setter;

public class BinaryResource implements Resource<String, BinaryEntity> {
	
	public final static int NEW_ENTITY = -1;
	public final static int DELETED = -2;


	@Getter
	private final File dir;
	private final Map<String,BinaryResourceItem> index;
	@Getter
	@Setter
	private String datafileExtension = "json";
	private final ThreadLocal<Checksum> checksum;
	private final KeyGenerator<String> generator;
	
	public BinaryResource(File dir, ThreadLocal<Checksum> checksum) throws BinaryResourceInitializeException {
		checkDir(dir);
		this.dir = dir;
		this.index = new HashMap<String, BinaryResourceItem>();
		this.checksum = checksum;
		this.generator = () -> {
			String newKey = RandomStringUtils.randomAlphanumeric(20);
			while (index.containsKey(newKey)) {
				newKey = RandomStringUtils.randomAlphanumeric(20);
			}
			return newKey;
		};
		loadResources();
	}
	
	public BinaryResource(File dir, ThreadLocal<Checksum> checksum, KeyGenerator<String> generator) throws BinaryResourceInitializeException {
		checkDir(dir);
		this.dir = dir;
		this.index = new HashMap<String, BinaryResourceItem>();
		this.checksum = checksum;
		this.generator = generator;
		loadResources();
	}
	
	private void checkDir(File dir) throws BinaryResourceInitializeException {
		if (! dir.exists()) throw new BinaryResourceInitializeException("Resource directory does not exist", dir);
		if (! dir.isDirectory()) throw new BinaryResourceInitializeException("Resource directory is not a directory", dir);
		if (! dir.canRead()) throw new BinaryResourceInitializeException("Resource directory is not readable", dir);
		if (! dir.canWrite()) throw new BinaryResourceInitializeException("Resource directory is not writable", dir);
			
	}
	
	private void loadResources() throws BinaryResourceInitializeException {
		for (File resourceFile : readResourceFiles(dir)) {
			try {
				BinaryResourceItem indexItem = new BinaryResourceItem(this, resourceFile, checksum.get());
				index.put(indexItem.getKey(), indexItem);
			} catch (IOException err) {
				throw new BinaryResourceInitializeException(MessageFormat.format(
						"Unable to read data file: {0} while initializing respurce: {0}",
						resourceFile,
						dir),
						dir);
			}
		}		
	}
	
	private Set<File> readResourceFiles(File dir) {
	    return Stream.of(dir.listFiles())
	    	      .filter(file -> !file.isDirectory())
	    	      .collect(Collectors.toSet());
	}

	@Override
	public BinaryEntity create(String key, BinaryEntity obj) throws DuplicateKeyError, MutatingEntityError {
		if (! obj.isNew()) throw new MutatingEntityError(key, "The RefItem is not new during create");
		if (obj.isDeleted()) throw new MutatingEntityError(key, "The RefItem is deleted during create");
		if (index.containsKey(key)) throw new DuplicateKeyError(key);
		obj.setKey(key);
		try {
			BinaryResourceItem newItem = new BinaryResourceItem(this, obj, checksum.get());
			index.put(key, newItem);
			return new BinaryEntity(newItem);
		} catch (IOException err) {
			throw new UnexpectedResourceError(
					MessageFormat.format("Unable to create new resource data file for entity with key: {0}",
							key), err);
		}
	}

	@Override
	public BinaryEntity get(String key) throws MissingKeyError {
		BinaryResourceItem item = index.get(key);
		if (item == null) {
			throw new MissingKeyError(key);
		}
		return new BinaryEntity(item);
	}

	@Override
	public BinaryEntity update(String key, BinaryEntity obj) throws MissingKeyError, MutatingEntityError {
		if (obj.isNew()) throw new MutatingEntityError(key, "The RefIten is new during update");
		if (obj.isDeleted()) throw new MutatingEntityError(key, "The RefItem is deleted during update");
		if (!index.containsKey(key)) throw new MissingKeyError(key);
		obj.setKey(key);
		BinaryResourceItem item = index.get(key);
		try {
			item.update(obj, checksum.get());
			return new BinaryEntity(item);
		} catch (IOException err) {
			throw new UnexpectedResourceError(
					MessageFormat.format("Unable to write updated resource data for entity with key: {0}", key), err);
		}
	}

	@Override
	public List<BinaryEntity> fetch() {
		List<BinaryEntity> result = new LinkedList<BinaryEntity>();
		index.values().stream()
				.forEach(item -> {
					result.add(new BinaryEntity(item));
				});
		return result;
	}

	@Override
	public BinaryEntity remove(String key) throws MissingKeyError, MutatingEntityError {
		if (!index.containsKey(key)) throw new MissingKeyError(key);
		BinaryResourceItem item = index.get(key);
		try {
			byte[] data = item.delete();
			index.remove(key);
			return new BinaryEntity(item, data);
		} catch (IOException err) {
			throw new UnexpectedResourceError(
					MessageFormat.format("Unable to remove entity with key: {0}", key), err);
		}
	}

	@Override
	public BinaryEntity save(BinaryEntity obj) throws MissingKeyError, MutatingEntityError, DuplicateKeyError {
		if (obj.isDeleted()) throw new MutatingEntityError(obj.getKey(), "The RefItem is deleted during save");
		if (obj.getKey() == null) {
			String key = generator.generate();
			obj.setKey(key);
		}
		if (obj.isNew()) {
			return create(obj.getKey(), obj);
		} else {
			return update(obj.getKey(), obj);
		}
	}
	
	@Override
	public int count() {
		return index.size();
	}
	
	@Override
	public boolean exists(String key) {
		return index.containsKey(key);
	}
	
	@Override
	public Set<String> keys() {
		return index.keySet();
	}

	@Override
	public void delete(BinaryEntity obj) throws MissingKeyError, MutatingEntityError {
		if (obj.isNew()) throw new MutatingEntityError(obj.getKey(), "The RefIten is new during delete");
		if (obj.isDeleted()) throw new MutatingEntityError(obj.getKey(), "The RefItem is deleted during delete");
		remove(obj.getKey());
		
	}

}
