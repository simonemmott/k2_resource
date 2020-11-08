package org.k2.resource.location;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.k2.resource.MetaResource;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.exception.UnexpectedResourceError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Getter;

import static org.apache.commons.io.filefilter.FileFilterUtils.*;

public class SimpleDigestableLocation implements DigestableLocation {
	
	@Getter
	private final File location;
	private final Digestor digestor;
	private final Map<String,DigestableResource> resources = new HashMap<>();
	private final Map<String,DigestableLocation> locations = new HashMap<>();
	private final File metadataFile;
	private final ObjectMapper metaMapper = new ObjectMapper(new YAMLFactory());
	private boolean digested = false;
	
	private static void checkLocation(File location) throws ResourceConfigurationException {
		if (! location.exists()) throw new ResourceConfigurationException(location, "does not exist!");
		if (! location.isDirectory()) throw new ResourceConfigurationException(location, "is not a directory!");
		if (! location.canRead()) throw new ResourceConfigurationException(location, "cannot be read!");
		if (! location.canWrite()) throw new ResourceConfigurationException(location, "cannot be written!");			
	}
	

	public SimpleDigestableLocation(File location, Digestor digestor) throws ResourceConfigurationException {
		checkLocation(location);
		this.location = location;
		this.digestor = digestor;
		this.metadataFile = FileUtils.getFile(location, "__meta__.yml");
	}
	
	public SimpleDigestableLocation(File location) throws ResourceConfigurationException {
		checkLocation(location);
		this.location = location;
		this.digestor = DigestableLocation.defaultDigestor();
		this.metadataFile = FileUtils.getFile(location, "__meta__.yml");
	}
	
	@Override
	public String getName() {
		return location.getName();
	}
	
	@Override
	public void refresh() {
		try {
			digest();
		} catch (ResourceConfigurationException e) {
			throw new UnexpectedResourceError("unable to refresh digestable location: "+location.getAbsolutePath(), e);
		}
	}
	
	@Override
	public void digest() throws ResourceConfigurationException {
		for (File file : FileUtils.listFiles(
				location,
				notFileFilter(nameFileFilter("__meta__.yml")), 
				falseFileFilter())) {
			DigestableResource resource = DigestableResource.create(this, file, digestor);
			resources.put(resource.getKey(), resource);
		}
		for (File file : FileUtils.listFilesAndDirs(
				location,
				falseFileFilter(), 
				trueFileFilter())) {
			if (!file.equals(location)) {
				DigestableLocation location = DigestableLocation.create(file, digestor);
				locations.put(location.getName(), location);
			}
		}
		digested = true;
	}

	@Override
	public boolean hasMetadata() {
		return metadataFile.exists();
	}

	@Override
	public <M extends MetaResource> M getMetadata(Class<M> metadataType) {
		if (!hasMetadata()) return null;
		try {
			return metaMapper.readValue(metadataFile, metadataType);
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to read metadata from: "+metadataFile.getAbsolutePath(), e);
		}
	}

	@Override
	public <M extends MetaResource> void setMetadata(M metadata) {
		try {
			metaMapper.writeValue(metadataFile, metadata);
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to write metadata to: "+metadataFile.getAbsolutePath(), e);
		}
		
	}
	
	private String getDatafileExtension() {
		if (! hasMetadata()) return "json";
		return getMetadata(MetaResource.class).getDatafileExtension();
	}

	@Override
	public Set<String> keys() {
		if (! digested) refresh();
		return resources.keySet();
	}

	@Override
	public DigestableResource getResource(String key) throws MissingKeyError {
		if (! digested) refresh();
		DigestableResource resource = resources.get(key);
		if (resource == null) throw new MissingKeyError(key, "At location: "+location.getAbsolutePath());
		return resource;
	}

	@Override
	public DigestableResource createResource(String key) throws DuplicateKeyError {
		if (! digested) refresh();
		if (resources.containsKey(key)) throw new DuplicateKeyError(key, "at location: "+location.getAbsolutePath());
		File newResourceFile = FileUtils.getFile(location, key+"."+getDatafileExtension());
		try {
			newResourceFile.createNewFile();
			DigestableResource resource = DigestableResource.create(this, newResourceFile, digestor);
			resources.put(resource.getKey(), resource);
			return resource;
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to create new resource file: "+newResourceFile.getAbsolutePath(), e);
		} catch (ResourceConfigurationException e) {
			throw new UnexpectedResourceError("Unable to create new resource with key: "+key+" at location: "+location.getAbsolutePath(), e);
		}
	}

	@Override
	public DigestableResource removeResource(String key) throws MissingKeyError {
		if (! digested) refresh();
		DigestableResource resource = resources.get(key);
		if (resource == null) throw new MissingKeyError(key, "at location: "+location.getAbsolutePath());
		resource.getDatafile().delete();
		resources.remove(key);
		return resource;
	}

	@Override
	public boolean resourceExists(String key) {
		if (! digested) refresh();
		return resources.containsKey(key);
	}

	@Override
	public Set<String> locations() {
		if (! digested) refresh();
		return locations.keySet();
	}

	@Override
	public DigestableLocation getLocation(String name) throws MissingKeyError {
		if (! digested) refresh();
		DigestableLocation childLoc = locations.get(name);
		if (childLoc == null) throw new MissingKeyError(name, "No child location with key found at location: "+location.getAbsolutePath());
		return childLoc;
	}

	@Override
	public DigestableLocation createLocation(String name) throws DuplicateKeyError {
		if (! digested) refresh();
		if (locations.containsKey(name)) throw new DuplicateKeyError(name, "A child location with key already exists at location: "+location.getAbsolutePath());
		File childLocationDir = FileUtils.getFile(location, name);
		try {
			FileUtils.forceMkdir(childLocationDir);
			DigestableLocation childLocation = DigestableLocation.create(childLocationDir, digestor);
			locations.put(childLocation.getName(), childLocation);
			return childLocation;
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to create new resource location directory with name: "+name+" at location: "+location.getAbsolutePath(), e);
		} catch (ResourceConfigurationException e) {
			throw new UnexpectedResourceError("Unable to create new resource location with name: "+name+" at location: "+location.getAbsolutePath(), e);
		}
	}

	@Override
	public <M extends MetaResource> DigestableLocation createLocation(String name, M metadata) throws DuplicateKeyError {
		DigestableLocation childLocation = createLocation(name);
		childLocation.setMetadata(metadata);
		return childLocation;
	}

	@Override
	public DigestableLocation removeLocation(String name) throws MissingKeyError {
		if (! digested) refresh();
		if (!locations.containsKey(name)) throw new MissingKeyError(name, "No child location with key exists at location: "+location.getAbsolutePath());
		DigestableLocation childLocation = locations.get(name);
		try {
			FileUtils.forceDelete(childLocation.getLocation());
			locations.remove(name);
			return childLocation;
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to remove resource location with name: "+name+" at location: "+location.getAbsolutePath(), e);
		}
	}

	@Override
	public boolean locationExists(String name) {
		if (! digested) refresh();
		return locations.containsKey(name);
	}

	@Override
	public void clean() {
		for (DigestableResource resource : resources.values()) {
			resource.getDatafile().delete();
		}
		
	}

	@Override
	public Collection<? extends DigestableResource> getResources() {
		return resources.values();
	}

}
