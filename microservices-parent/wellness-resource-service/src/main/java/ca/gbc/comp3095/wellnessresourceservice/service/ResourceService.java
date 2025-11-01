package ca.gbc.comp3095.wellnessresourceservice.service;


import ca.gbc.comp3095.wellnessresourceservice.model.Resource;
import ca.gbc.comp3095.wellnessresourceservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "wellness_resources")
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @CachePut(key = "#result.id")
    public Resource saveResource(Resource resource) {

        return resourceRepository.save(resource);
    }

    @Cacheable(key = "'all'")
    public List<Resource> getAllResources(){

        return resourceRepository.findAll();
    }

    @Cacheable(key = "#id")
    public Optional<Resource> getResourceById(Long id){

        return resourceRepository.findById(id);
    }

    @CachePut(key = "#id")
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

    // Use the new case-insensitive query
    @Cacheable(key = "'category:' + #category.toLowerCase()")
    public List<Resource> getResourcesByCategory(String category) {
        return resourceRepository.findByCategoryIgnoreCase(category);
    }


    @CacheEvict(key = "#id", allEntries = false)
    public void deleteResource(Long id) {

        resourceRepository.deleteById(id);
    }
}
