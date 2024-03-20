package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.view.ReviewDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Review;
import lv.emendatus.Destiny_PropMan.mapper.ReviewMapper;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ReviewRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaReviewService implements ReviewService {

    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    public JpaReviewService(ReviewRepository repository, ReviewMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    @Override
    public List<Review> getAllReviews() {
        return repository.findAll();
    }
    @Override
    public Optional<Review> getReviewById(Long id) {
        return repository.findById(id);
    }
    @Override
    public void addReview(Review review) {
        repository.save(review);
    }
    @Override
    public void deleteReview(Long id) {
        repository.deleteById(id);
    }
    @Override
    public List<Review> findByPropertyId(Long prop_id) {
        return getAllReviews().stream().filter(review -> review.getPropertyId().equals(prop_id)).toList();
    }
    @Override
    public void leaveReview(ReviewDTO reviewDTO) {
        Review review = mapper.toEntity(reviewDTO);
        repository.save(review);
    }
    @Override
    public List<ReviewDTO> getPropertyReviews(Long propertyId) {
        List<Review> reviews = findByPropertyId(propertyId);
        return reviews.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
    public ReviewMapper getMapper() {
        return mapper;
    }
}
