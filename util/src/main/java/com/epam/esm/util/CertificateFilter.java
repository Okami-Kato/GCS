package com.epam.esm.util;

/**
 * Storage for filter properties to be applied, when searching for certificates
 */
public class CertificateFilter {
    /**
     * Substring in name
     */
    private String namePart;
    /**
     * Substring in description
     */
    private String descriptionPart;
    /**
     * Assigned tags (and condition)
     */
    private String[] tagNames;
    /**
     * Object, encapsulating sort properties and directions
     */
    private Sort sort;

    public CertificateFilter() {
    }

    public static CertificateFilterBuilder newBuilder() {
        return new CertificateFilter().new CertificateFilterBuilder();
    }

    public String getNamePart() {
        return namePart;
    }

    public String[] getTagNames() {
        return tagNames;
    }

    public String getDescriptionPart() {
        return descriptionPart;
    }

    public Sort getSort() {
        return sort;
    }

    public class CertificateFilterBuilder {
        private CertificateFilterBuilder() {
        }

        public CertificateFilterBuilder withPartInName(String namePart) {
            CertificateFilter.this.namePart = namePart;
            return this;
        }

        public CertificateFilterBuilder withPartInDescription(String descriptionPart) {
            CertificateFilter.this.descriptionPart = descriptionPart;
            return this;
        }

        public CertificateFilterBuilder withTags(String... tagNames) {
            CertificateFilter.this.tagNames = tagNames;
            return this;
        }

        public CertificateFilterBuilder withSort(Sort sort) {
            CertificateFilter.this.sort = sort;
            return this;
        }

        public CertificateFilter build() {
            CertificateFilter certificateFilter = new CertificateFilter();
            certificateFilter.namePart = CertificateFilter.this.namePart;
            certificateFilter.descriptionPart = CertificateFilter.this.descriptionPart;
            certificateFilter.tagNames = CertificateFilter.this.tagNames;
            certificateFilter.sort = CertificateFilter.this.sort;
            return certificateFilter;
        }
    }
}
