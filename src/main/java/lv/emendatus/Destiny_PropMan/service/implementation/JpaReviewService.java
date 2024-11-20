package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.view.ReviewDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Review;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;
import lv.emendatus.Destiny_PropMan.exceptions.EntityNotFoundException;
import lv.emendatus.Destiny_PropMan.mapper.ReviewMapper;
import lv.emendatus.Destiny_PropMan.repository.interfaces.ReviewRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.ReviewService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaReviewService implements ReviewService {

    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    private final JpaTenantService tenantService;
    private final JpaBookingService bookingService;
    private final JpaLeasingHistoryService leasingHistoryService;
    public JpaReviewService(ReviewRepository repository, ReviewMapper mapper, JpaTenantService tenantService, JpaBookingService bookingService, JpaLeasingHistoryService leasingHistoryService) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantService = tenantService;
        this.bookingService = bookingService;
        this.leasingHistoryService = leasingHistoryService;
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
    @Transactional
    @PreAuthorize("hasAuthority('TENANT')")
    public void leaveReview(ReviewDTO reviewDTO, Principal principal) {
        String authenticatedUserName = principal.getName();
        Tenant tenant = tenantService.getTenantByLogin(authenticatedUserName);
        if (tenant != null) {
            Review review = mapper.toEntity(reviewDTO);
            Long propertyId = review.getPropertyId();
            boolean tenantEverStayedInProperty = false;
            for (Booking booking : bookingService.getBookingsByTenant(tenant)) {
                if (booking.getProperty().getId().equals(propertyId) &&
                        (booking.getStatus().equals(BookingStatus.OVER) || booking.getStatus().equals(BookingStatus.CURRENT)))
                                tenantEverStayedInProperty = true;
            }
            for (LeasingHistory history : leasingHistoryService.getLeasingHistoryByTenant(tenant)) {
                if (history.getPropertyId().equals(propertyId) && history.getTenant().getId().equals(tenant.getId())) tenantEverStayedInProperty = true;
            }
            boolean tenantAlreadyReviewedTheProperty = false;
            if (tenantEverStayedInProperty) {
                for (ReviewDTO previousReview : getPropertyReviews(propertyId)) {
                    if (previousReview.getTenantId().equals(tenant.getId())) {
                        tenantAlreadyReviewedTheProperty = true;
                    }
                }
                if (tenantAlreadyReviewedTheProperty) {
                    throw new AccessDeniedException("You have already rated this Property");
                } else {
                    repository.save(review);
                }
            } else {
                throw new AccessDeniedException("A Tenant may only rate a Property where he/she ever stayed");
            }
        } else {
            throw new EntityNotFoundException("Tenant retrieval failed");
        }
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
