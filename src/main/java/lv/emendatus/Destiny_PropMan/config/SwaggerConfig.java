package lv.emendatus.Destiny_PropMan.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*

      USE http://localhost:8080/swagger-ui/index.html TO ACCESS SWAGGER UI!

 */


@OpenAPIDefinition(
        info = @Info(
                title = "Property Management - graduation project",
                description = "The aim of the project is to develop a backend component for a real property rental management system, akin to airbnb or similar. <br>" +
                        "The overall data structure is as follows:<br>"
                        + "I. User entities:<br>"
                        + "  1) Admin - the user/users in charge of managing the system<br>"
                        + "  2) Manager - a user who rents out a property or several properties at the platform<br>"
                        + "  3) Tenant - a user renting properties via the platform for remuneration<br>"
                        + "II. Rental process entities:<br>"
                        + "  1) Property - a property available for rent<br>"
                        + "  2) Booking - a booking of a specific Property made for a specified period in the future<br>"
                        + "  3) Amenity - any amenity that a Property might have<br>"
                        + "  4) LeasingHistory -  a summary of past Bookings for a specific Tenant<br>"
                        + "  5) TenantFavorites - a collection of a specific Tenant's favorite Properties<br>"
                        + "  6) EarlyTerminationRequest - a current Tenant's request to the respective Manager to terminate the Booking prematurely<br>"
                        + "III. Finance-related entities:<br>"
                        + "  1) Bill - any bill that a Manager might need to pay incidental to the keeping of his/her property. Not directly involved in the rental process<br>"
                        + "  2) Currency - a class designating a currency for transactions<br>"
                        + "  3) Payout - money paid by the system to a Manager after a Booking is over<br>"
                        + "  4) PropertyDiscount - a discount that a Manager can configure for a Property, applying to a certain time period<br>"
                        + "  5) Refund - money paid by the system back to a Tenant in case of cancellation or premature termination of a Booking<br>"
                        + "  6) TenantPayment - a Tenant's payment for a specific Booking<br>"
                        + "IV. Communication entities:<br>"
                        + "  1) Claim - a claim that can be lodged by a Tenant against a Manager, or vice versa<br>"
                        + "  2) Message - a message sent by a Tenant to a Manager or vice versa<br>"
                        + "  3) Review - a review of a Property that a Tenant can leave after a Booking is over <br>"
                        + "  4) PropertyRating - a separate entity contributing to the overall rating of each specific Property. <br>"
                        + "  5) TenantRating - a separate entity contributing to the overall rating of each specific Tenant. <br>"
                        + "V. Auxiliary entities:<br>"
                        + "  1) Numerical config - used to store various numerical values, including some system settings<br>"
                        + "  2) PropertyAmenity - an auxiliary entity linking Properties and Amenities<br>"
                        + "  3) TokenResetter - an auxiliary entity employed in the process of resetting email confirmation and password reset tokens<br>"
                        + "  4) NumericDataMapping - a three-layered map used to store keys for decrypting users' sensitive financial data, specifically, payment card numbers and CVV codes<br>"
                        + "  5) KeyLink - an auxiliary entity specifying which file to browse for a specific user's encrypted records<br>"
                        + "  6) PropertyLock - an auxiliary entity required in order to allow a Manager to unlock any of his/her Properties that might have been locked for a specific period previously<br>"
                        ,
                version = "1.0.0",
                contact = @Contact(
                        name = "Sergejs Ponomarenko",
                        url = "https://www.emendatus.lv/"
                )
        )
)
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String AUTHORIZATION = "Authorization and/or user management service";
    public static final String GENERAL = "Generally available service";
    public static final String ADMIN_FUNCTION = "Admin functionality service";
    public static final String MANAGER_FUNCTION = "Manager functionality service";
    public static final String TENANT_FUNCTION = "Tenant functionality service";
    public static final String OTHER = "Miscellaneous";


}
