package com.epam.esm.util;

public class CertificateFilter {
    private int pageNumber;
    private int pageSize;
    private String namePart;
    private String descriptionPart;
    private String[] tagNames;
    private Sort sort;

    public CertificateFilter() {
    }

    public static CertificateFilterBuilder newBuilder() {
        return new CertificateFilter().new CertificateFilterBuilder();
    }

    public String getNamePart() {
        return namePart;
    }

    public String getDescriptionPart() {
        return descriptionPart;
    }

    public String[] getTagNames() {
        return tagNames;
    }

    public Sort getSort() {
        return sort;
    }

    public class CertificateFilterBuilder {
        private CertificateFilterBuilder() {
        }

        public CertificateFilterBuilder fromPageNumber(int pageNumber) {
            CertificateFilter.this.pageNumber = pageNumber;
            return this;
        }

        public CertificateFilterBuilder withPageSize(int pageSize) {
            CertificateFilter.this.pageSize = pageSize;
            return this;
        }

        public CertificateFilterBuilder withPartInName(String namePart) {
            CertificateFilter.this.namePart = namePart;
            return this;
        }

        public CertificateFilterBuilder withPartInDescription(String descriptionPart) {
            CertificateFilter.this.descriptionPart = descriptionPart;
            return this;
        }

        public CertificateFilterBuilder withTags(String[] tagNames) {
            CertificateFilter.this.tagNames = tagNames;
            return this;
        }

        public CertificateFilterBuilder sortBy(Sort sort) {
            CertificateFilter.this.sort = sort;
            return this;
        }

        public CertificateFilter build() {
            CertificateFilter certificateFilter = new CertificateFilter();
            certificateFilter.pageNumber = CertificateFilter.this.pageNumber;
            certificateFilter.pageSize = CertificateFilter.this.pageSize;
            certificateFilter.namePart = CertificateFilter.this.namePart;
            certificateFilter.descriptionPart = CertificateFilter.this.descriptionPart;
            certificateFilter.tagNames = CertificateFilter.this.tagNames;
            certificateFilter.sort = CertificateFilter.this.sort;
            return certificateFilter;
        }
    }
}
