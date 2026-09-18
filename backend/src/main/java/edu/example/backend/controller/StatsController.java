package edu.example.backend.controller;

import edu.example.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
  private final StatsService statsService;

  @GetMapping("/overview")
  public Map<String,Object> overview(){
    return statsService.overview();
  }
}
