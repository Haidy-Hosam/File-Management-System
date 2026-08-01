package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.Model.Page;
import com.ADIB.FileSystem.DataAccess.repository.PageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Pages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PageController {
    private final PageRepo pageRepo;

    @GetMapping
    public List<Page> getAllPages() {
        return pageRepo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Page createPage(@RequestBody Page page) {
        return pageRepo.save(page);
    }

    @DeleteMapping("/{pageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePage(@PathVariable Long pageId) {
        pageRepo.deleteById(pageId);
    }
}
