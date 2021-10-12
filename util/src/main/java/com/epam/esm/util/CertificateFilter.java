package com.epam.esm.util;

public class CertificateFilter {
    private String namePart;
    private String descriptionPart;
    private Integer[] tagIds;
    private Sort sort;

    public CertificateFilter() {
    }

    public static CertificateFilterBuilder newBuilder() {
        return new CertificateFilter().new CertificateFilterBuilder();
    }

    public String getNamePart() {
        return namePart;
    }

    public Integer[] getTagIds() {
        return tagIds;
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

        public CertificateFilterBuilder withTags(Integer... tagIds) {
            CertificateFilter.this.tagIds = tagIds;
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
            certificateFilter.tagIds = CertificateFilter.this.tagIds;
            certificateFilter.sort = CertificateFilter.this.sort;
            return certificateFilter;
        }
    }
}
