package org.egov.restapi.util;

import java.util.List;

import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.model.ErrorDetails;
import org.egov.ptis.domain.model.FloorDetails;
import org.egov.ptis.domain.model.OwnerDetails;
import org.egov.restapi.constants.RestApiConstants;
import org.egov.restapi.model.AssessmentsDetails;
import org.egov.restapi.model.CreatePropertyDetails;
import org.egov.restapi.model.SurroundingBoundaryDetails;
import org.egov.restapi.model.VacantLandDetails;

public class ValidationUtil {
	public static ErrorDetails validateCreateRequest(CreatePropertyDetails createPropDetails) {
		ErrorDetails errorDetails = null;
		String propertyTypeMasterCode = createPropDetails.getPropertyTypeMasterCode();
		if (propertyTypeMasterCode == null || propertyTypeMasterCode.trim().length() == 0) {
			errorDetails = new ErrorDetails();
			errorDetails.setErrorCode(RestApiConstants.OWNERSHIP_CATEGORY_TYPE_REQ_CODE);
			errorDetails.setErrorMessage(RestApiConstants.OWNERSHIP_CATEGORY_TYPE_REQ_MSG);
			return errorDetails;
		}
		if (propertyTypeMasterCode != null
				&& !propertyTypeMasterCode.equalsIgnoreCase(PropertyTaxConstants.OWNERSHIP_TYPE_VAC_LAND)
				&& !propertyTypeMasterCode.equalsIgnoreCase(PropertyTaxConstants.OWNERSHIP_TYPE_PRIVATE)
				&& !propertyTypeMasterCode.equalsIgnoreCase(PropertyTaxConstants.OWNERSHIP_TYPE_STATE_GOVT)
				&& !propertyTypeMasterCode.equalsIgnoreCase(PropertyTaxConstants.OWNERSHIP_TYPE_CENTRAL_GOVT_335)
				&& !propertyTypeMasterCode.equalsIgnoreCase(PropertyTaxConstants.OWNERSHIP_TYPE_CENTRAL_GOVT_50)
				&& !propertyTypeMasterCode.equalsIgnoreCase(PropertyTaxConstants.OWNERSHIP_TYPE_CENTRAL_GOVT_75)) {
			errorDetails = new ErrorDetails();
			errorDetails.setErrorCode(RestApiConstants.OWNERSHIP_CATEGORY_TYPE_INVALID_CODE);
			errorDetails.setErrorMessage(RestApiConstants.OWNERSHIP_CATEGORY_TYPE_INVALID_MSG);
			return errorDetails;
		}
		String propertyCategoryCode = createPropDetails.getPropertyCategoryCode();
		if (propertyCategoryCode == null || propertyCategoryCode.trim().length() == 0) {
			errorDetails = new ErrorDetails();
			errorDetails.setErrorCode(RestApiConstants.PROPERTY_CATEGORY_TYPE_REQ_CODE);
			errorDetails.setErrorMessage(RestApiConstants.PROPERTY_CATEGORY_TYPE_REQ_MSG);
			return errorDetails;
		}
		if (propertyCategoryCode != null
				&& !propertyCategoryCode.equalsIgnoreCase(PropertyTaxConstants.CATEGORY_RESIDENTIAL)
				&& !propertyCategoryCode.equalsIgnoreCase(PropertyTaxConstants.CATEGORY_NON_RESIDENTIAL)
				&& !propertyCategoryCode.equalsIgnoreCase(PropertyTaxConstants.CATEGORY_MIXED)) {
			errorDetails = new ErrorDetails();
			errorDetails.setErrorCode(RestApiConstants.PROPERTY_CATEGORY_TYPE_INVALID_CODE);
			errorDetails.setErrorMessage(RestApiConstants.PROPERTY_CATEGORY_TYPE_INVALID_MSG);
			return errorDetails;
		}

		List<OwnerDetails> ownerDetailsList = createPropDetails.getOwnerDetails();
		if (ownerDetailsList == null) {
			errorDetails = new ErrorDetails();
			errorDetails.setErrorCode(RestApiConstants.OWNER_DETAILS_REQ_CODE);
			errorDetails.setErrorMessage(RestApiConstants.OWNER_DETAILS_REQ_MSG);
			return errorDetails;
		} else {
			for (OwnerDetails ownerDetails : ownerDetailsList) {
				if (ownerDetails.getMobileNumber() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.MOBILE_NO_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.MOBILE_NO_REQ_MSG);
					return errorDetails;
				}
				if (ownerDetails.getName() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.OWNER_NAME_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.OWNER_NAME_REQ_MSG);
					return errorDetails;
				}
				if (ownerDetails.getGender() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.GENDER_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.GENDER_REQ_MSG);
					return errorDetails;
				}
				if (ownerDetails.getGuardianRelation() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.GUARDIAN_RELATION_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.GUARDIAN_RELATION_REQ_MSG);
					return errorDetails;
				}
				if (ownerDetails.getGuardian() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.GUARDIAN_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.GUARDIAN_REQ_MSG);
					return errorDetails;
				}
			}
		}

		AssessmentsDetails assessmentsDetails = createPropDetails.getAssessmentDetails();
		if (assessmentsDetails == null) {
			errorDetails = new ErrorDetails();
			errorDetails.setErrorCode(RestApiConstants.ASSESSMENT_DETAILS_REQ_CODE);
			errorDetails.setErrorMessage(RestApiConstants.ASSESSMENT_DETAILS_REQ_MSG);
			return errorDetails;
		} else {
			if (assessmentsDetails.getMutationReasonCode() == null) {
				errorDetails = new ErrorDetails();
				errorDetails.setErrorCode(RestApiConstants.REASON_FOR_CREATION_REQ_CODE);
				errorDetails.setErrorMessage(RestApiConstants.REASON_FOR_CREATION_REQ_MSG);
				return errorDetails;
			}
			if (assessmentsDetails.getExtentOfSite() == null) {
				errorDetails = new ErrorDetails();
				errorDetails.setErrorCode(RestApiConstants.EXTENT_OF_SITE_REQ_CODE);
				errorDetails.setErrorMessage(RestApiConstants.EXTENT_OF_SITE_REQ_MSG);
				return errorDetails;
			}
			if (assessmentsDetails.getRegdDocNo() == null) {
				errorDetails = new ErrorDetails();
				errorDetails.setErrorCode(RestApiConstants.REG_DOC_NO_REQ_CODE);
				errorDetails.setErrorMessage(RestApiConstants.REG_DOC_NO_REQ_MSG);
				return errorDetails;
			}
			if (assessmentsDetails.getRegdDocDate() == null) {
				errorDetails = new ErrorDetails();
				errorDetails.setErrorCode(RestApiConstants.REG_DOC_DATE_REQ_CODE);
				errorDetails.setErrorMessage(RestApiConstants.REG_DOC_DATE_REQ_MSG);
				return errorDetails;
			}
			if (assessmentsDetails.getIsExtentAppurtenantLand()) {
				VacantLandDetails vacantLandDetails = createPropDetails.getVacantLandDetails();
				if (vacantLandDetails == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.VACANT_LAND_DETAILS_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.VACANT_LAND_DETAILS_REQ_MSG);
					return errorDetails;
				} else {
					if (vacantLandDetails.getSurveyNumber() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.SURVEY_NO_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.SURVEY_NO_REQ_MSG);
						return errorDetails;
					}
					if (vacantLandDetails.getPattaNumber() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.PATTA_NO_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.PATTA_NO_REQ_MSG);
						return errorDetails;
					}
					if (vacantLandDetails.getVacantLandArea() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.VACANT_LAND_AREA_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.VACANT_LAND_AREA_REQ_MSG);
						return errorDetails;
					}
					if (vacantLandDetails.getMarketValue() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.MARKET_AREA_VALUE_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.MARKET_AREA_VALUE_REQ_MSG);
						return errorDetails;
					}
					if (vacantLandDetails.getCurrentCapitalValue() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.CURRENT_CAPITAL_VALUE_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.CURRENT_CAPITAL_VALUE_REQ_MSG);
						return errorDetails;
					}
					if (vacantLandDetails.getEffectiveDate() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.EFFECTIVE_DATE_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.EFFECTIVE_DATE_REQ_MSG);
						return errorDetails;
					}
				}

				SurroundingBoundaryDetails surBoundaryDetails = createPropDetails.getSurroundingBoundaryDetails();
				if (surBoundaryDetails == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.SURROUNDING_BOUNDARY_DETAILS_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.SURROUNDING_BOUNDARY_DETAILS_REQ_MSG);
					return errorDetails;
				} else {
					if (surBoundaryDetails.getNorthBoundary() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.NORTH_BOUNDARY_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.NORTH_BOUNDARY_REQ_MSG);
						return errorDetails;
					}
					if (surBoundaryDetails.getSouthBoundary() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.SOUTH_BOUNDARY_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.SOUTH_BOUNDARY_REQ_MSG);
						return errorDetails;
					}
					if (surBoundaryDetails.getEastBoundary() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.EAST_BOUNDARY_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.EAST_BOUNDARY_REQ_MSG);
						return errorDetails;
					}
					if (surBoundaryDetails.getWestBoundary() == null) {
						errorDetails = new ErrorDetails();
						errorDetails.setErrorCode(RestApiConstants.WEST_BOUNDARY_REQ_CODE);
						errorDetails.setErrorMessage(RestApiConstants.WEST_BOUNDARY_REQ_MSG);
						return errorDetails;
					}
				}
			}
		}

		List<FloorDetails> floorDetailsList = createPropDetails.getFloorDetails();
		if (floorDetailsList == null) {
			errorDetails = new ErrorDetails();
			errorDetails.setErrorCode(RestApiConstants.FLOOR_DETAILS_REQ_CODE);
			errorDetails.setErrorMessage(RestApiConstants.FLOOR_DETAILS_REQ_MSG);
			return errorDetails;
		} else {
			for (FloorDetails floorDetails : floorDetailsList) {
				if (floorDetails.getFloorNoCode() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.FLOOR_NO_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.FLOOR_NO_REQ_MSG);
					return errorDetails;
				}
				if (floorDetails.getBuildClassificationCode() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.CLASSIFICATION_OF_BUILDING_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.CLASSIFICATION_OF_BUILDING_REQ_MSG);
					return errorDetails;
				}
				if (floorDetails.getNatureOfUsageCode() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.NATURE_OF_USAGES_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.NATURE_OF_USAGES_REQ_MSG);
					return errorDetails;
				}
				if (floorDetails.getOccupancyCode() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.OCCUPANCY_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.OCCUPANCY_REQ_MSG);
					return errorDetails;
				}
				if (floorDetails.getConstructionDate() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.CONSTRUCTION_DATE_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.CONSTRUCTION_DATE_REQ_MSG);
					return errorDetails;
				}
				if (floorDetails.getPlinthArea() == null) {
					errorDetails = new ErrorDetails();
					errorDetails.setErrorCode(RestApiConstants.PLINTH_AREA_REQ_CODE);
					errorDetails.setErrorMessage(RestApiConstants.PLINTH_AREA_REQ_MSG);
					return errorDetails;
				}
			}
		}
		return errorDetails;
	}
}