package dev.demo.spring_boot_with_mongodb.exception;

/**
 * Exception thrown when a requested resource (e.g., Student, Department)
 * cannot be found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Name of the resource type, e.g. "Student" or "Department".
     */
    private final String resourceName;
    /**
     * Name of the field used to look up the resource, e.g. "id".
     */
    private final String fieldName;
    /**
     * Value of the lookup field that was not found.
     */
    private final String fieldValue;

    /**
     * Constructs a new ResourceNotFoundException.
     *
     * @param resourceName the type of the resource (for error message)
     * @param fieldName    the name of the field used in lookup
     * @param fieldValue   the value of the field that caused the lookup to fail
     */
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s : '%s'",
                resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * @return the resource type name that was not found
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return the field name used in the lookup
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return the lookup field value that was not found
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}