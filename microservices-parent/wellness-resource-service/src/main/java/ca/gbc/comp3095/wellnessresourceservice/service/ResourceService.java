package ca.gbc.comp3095.wellnessresourceservice.service;


import ca.gbc.comp3095.wellnessresourceservice.model.Resource;
import ca.gbc.comp3095.wellnessresourceservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource saveResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Cacheable(value = "resources")
    public List<Resource> getAllResources(){
        return resourceRepository.findAll();
    }


    public Optional<Resource> getResourceById(Long id){
        return resourceRepository.findById(id);
    }

    public Resource updateResource(Long id, Resource updatedResource) {
        return resourceRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedResource.getTitle());
                    existing.setDescription(updatedResource.getDescription());
                    existing.setCategory(updatedResource.getCategory());
                    existing.setUrl(updatedResource.getUrl());
                    return resourceRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }


    @CacheEvict(value = "resources", allEntries = true)
    public void deleteResource(Long id){
        resourceRepository.deleteById(id);
    }
}
