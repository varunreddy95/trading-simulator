package com.varun.appbackend.service;

import com.varun.appbackend.dto.PositionResponseDTO;
import com.varun.appbackend.model.Position;
import com.varun.appbackend.model.User;
import com.varun.appbackend.repository.PositionRepository;
import com.varun.appbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to manage user stock holdings (positions)
 */
@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public PositionService(PositionRepository positionRepository, UserRepository userRepository) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Returns all stock positions held by the user
     *
     * @param usedId the user ID
     * @returns list of stock symbol and quantity
     */
    public List<PositionResponseDTO> getUserPositions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<Position> positions = positionRepository.findByUser(user);

        return positions.stream()
                .map(pos -> new PositionResponseDTO(pos.getStockSymbol(), pos.getQuantity()))
                .collect(Collectors.toList());
    }
}
