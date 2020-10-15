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

import org.k2.resource.Resource;
import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;

import lombok.Getter;
import lombok.Setter;

public class BinaryResource implements Resource<String, BinaryEntity> {

	@Getter
	private final File dir;
	private final Map<String,BinaryResourceItem> index;
	@Getter
	@Setter
	private String datafileExtension = "json";
	private final ThreadLocal<Checksum> checksum;
	
	public BinaryResource(File dir, ThreadLocal<Checksum> checksum) throws BinaryResourceInitializeException {
		
		if (! dir.exists()) throw new BinaryResourceInitializeException("Resource directory does not exist", dir);
		if (! dir.isDirectory()) throw new BinaryResourceInitializeException("Resource directory is not a directory", dir);
		if (! dir.canRead()) throw new BinaryResourceInitializeException("Resource directory is not readable", dir);
		if (! dir.canWrite()) throw new BinaryResourceInitializeException("Resource directory is not writable", dir);
		
		this.dir = dir;
		this.index = new HashMap<String, BinaryResourceItem>();
		this.checksum = checksum;
		
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
	public BinaryEntity create(String key, BinaryEntity obj) throws DuplicateKeyError {
		obj.setKey(key);
		try {
			BinaryResourceItem newItem = new BinaryResourceItem(this, obj, checksum.get());
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
			throw new MissingKeyError(BinaryEntity.class, key);
		}
		return new BinaryEntity(item);
	}

	@Override
	public BinaryEntity update(String key, BinaryEntity obj) throws MissingKeyError, MutatingEntityError {
		// TODO Auto-generated method stub
		return null;
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
	public BinaryEntity delete(String key) throws MissingKeyError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BinaryEntity save(BinaryEntity obj) throws MissingKeyError, MutatingEntityError {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int count() {
		return index.size();
	}
	
	@Override
	public boolean exists(String key) {
		return index.containsKey(key);
	}

}
