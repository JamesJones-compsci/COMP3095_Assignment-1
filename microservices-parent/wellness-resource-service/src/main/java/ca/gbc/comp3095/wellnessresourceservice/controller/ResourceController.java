package ca.gbc.comp3095.wellnessresourceservice.controller;


import ca.gbc.comp3095.wellnessresourceservice.model.Resource;
import ca.gbc.comp3095.wellnessresourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {


    private final ResourceService resourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Resource createResource(@RequestBody Resource resource) {
        return resourceService.saveResource(resource);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Resource> getAllResources(){
        return resourceService.getAllResources();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Resource getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    // Get resources by category
    @GetMapping("/category/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<Resource> getByCategory(@PathVariable String category) {
        return resourceService.getResourcesByCategory(category);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateResource(@PathVariable Long id, @RequestBody Resource updatedResource) {
        resourceService.updateResource(id, updatedResource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
    }
}
