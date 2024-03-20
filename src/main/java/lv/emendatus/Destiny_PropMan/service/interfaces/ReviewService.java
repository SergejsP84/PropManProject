package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.view.ReviewDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import lv.emendatus.Destiny_PropMan.domain.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> getAllReviews();
    Optional<Review> getReviewById(Long id);
    void addReview(Review review);
    void deleteReview(Long id);
    List<Review> findByPropertyId (Long prop_id);
    void leaveReview(ReviewDTO reviewDTO);
    List<ReviewDTO> getPropertyReviews(Long propertyId);
}
