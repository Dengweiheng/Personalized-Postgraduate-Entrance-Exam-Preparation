package com.server.server.module.ability.controller;

import com.server.server.common.response.ApiResponse;
import com.server.server.module.ability.entity.AbilityScore;
import com.server.server.module.ability.service.AbilityScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ability")
@RequiredArgsConstructor
public class AbilityScoreController {

    private final AbilityScoreService abilityScoreService;

    @GetMapping("/latest")
    public ApiResponse<AbilityScore> latest(@RequestParam Long userId) {
        return ApiResponse.ok(abilityScoreService.latestByUser(userId));
    }

    @GetMapping("/history")
    public ApiResponse<List<AbilityScore>> history(@RequestParam Long userId) {
        return ApiResponse.ok(abilityScoreService.historyByUser(userId));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@Valid @RequestBody AbilityScore score) {
        return ApiResponse.ok(abilityScoreService.save(score));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody AbilityScore score) {
        return ApiResponse.ok(abilityScoreService.updateById(score));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.ok(abilityScoreService.removeById(id));
    }
}
