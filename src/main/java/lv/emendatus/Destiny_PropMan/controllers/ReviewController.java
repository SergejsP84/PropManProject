package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.review_controller.GetReviewsByProperty;
import lv.emendatus.Destiny_PropMan.annotation.review_controller.LeaveReview;
import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.view.ReviewDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final JpaReviewService service;

    public ReviewController(JpaReviewService service) {
        this.service = service;
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('TENANT')")
    @LeaveReview
    public ResponseEntity<Void> leaveReview(@RequestBody ReviewDTO review, Principal principal) {
        service.leaveReview(review, principal);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getByProperty/{propertyId}")
    @GetReviewsByProperty
    public ResponseEntity<List<ReviewDTO>> getReviewsByProperty(@PathVariable Long propertyId) {
        List<ReviewDTO> reviews = service.getPropertyReviews(propertyId);
        if (!reviews.isEmpty()) {
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
