package com.varun.appbackend.controller;

import com.varun.appbackend.dto.PositionResponseDTO;
import com.varun.appbackend.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * REST controller to expose user's current stock positions
 */
@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    /**
     * GET endpoint to fetch user' stock holdings
     *
     * @param userId ID of the user
     * @return list of holdings (stock symbol + quantity)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PositionResponseDTO>> getUserPositions(@PathVariable Long userId) {
        List<PositionResponseDTO> positions = positionService.getUserPositions(userId);
        return ResponseEntity.ok(positions);
    }
}
