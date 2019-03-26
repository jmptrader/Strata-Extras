/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.extra.repo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableConstructor;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.date.DayCount;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.product.LegalEntityId;
import com.opengamma.strata.product.ResolvedProduct;
import com.opengamma.strata.product.SecurityId;

/**
 * A repo, resolved for pricing.
 * <p>
 * This is the resolved form of {@link Repo} and is an input to the pricers.
 * Applications will typically create a {@code ResolvedRepo} from a {@code Repo}
 * using {@link Repo#resolve(ReferenceData)}.
 * <p>
 * A {@code ResolvedRepo} is bound to data that changes over time, such as holiday calendars.
 * If the data changes, such as the addition of a new holiday, the resolved form will not be updated.
 * Care must be taken when placing the resolved form in a cache or persistence layer.
 */
@BeanDefinition
public final class ResolvedRepo
    implements ResolvedProduct, ImmutableBean, Serializable {

  /**
   * The security identifiers of the collateral.
   * <p>
   * The identifier uniquely identifies the security within the system.
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableList<SecurityId> securityIds;
  /**
   * The legal entity identifier of the collateral.
   * <p>
   * This represents an legal entity associated with the collateral of the repo instrument, 
   * e.g., issuer of a fixed coupon bond.
   */
  @PropertyDefinition(validate = "notNull")
  private final LegalEntityId legalEntityId;
  /**
   * The primary currency.
   * <p>
   * This is the currency of the principal and the currency that payment is made in.
   */
  @PropertyDefinition(validate = "notNull")
  private final Currency currency;
  /**
   * The notional amount.
   * <p>
   * It is a positive signed amount if the repo is 'Buy',
   * and a negative signed amount if the repo is 'Sell'.
   * <p>
   * The currency of the notional is specified by {@code currency}.
   */
  @PropertyDefinition
  private final double notional;
  /**
   * The start date of the repo.
   * <p>
   * This is the first date that interest accrues.
   * <p>
   * This is an adjusted date, which should be a valid business day
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate startDate;
  /**
   * The end date of the repo.
   * <p>
   * This is the last day that interest accrues.
   * This date must be after the start date.
   * <p>
   * This is an adjusted date, which should be a valid business day
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate endDate;
  /**
   * The year fraction between the start and end date.
   * <p>
   * The value is usually calculated using a {@link DayCount}.
   * Typically the value will be close to 1 for one year and close to 0.5 for six months.
   * The fraction may be greater than 1, but not less than 0.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegative")
  private final double yearFraction;
  /**
   * The fixed rate of interest.
   * A 5% rate will be expressed as 0.05.
   */
  @PropertyDefinition(validate = "notNull")
  private final double rate;
  /**
   * The interest to be paid on the repo.
   * <p>
   * The interest is {@code rate * principal * yearFraction} and is a signed amount.
   */
  private final transient double interest;  // not a property

  //-------------------------------------------------------------------------
  /**
   * Gets the accrued interest.
   * <p>
   * The interest is {@code rate * principal * yearFraction}.
   * 
   * @return the accrued interest
   */
  public double getInterest() {
    return interest;
  }

  //-------------------------------------------------------------------------
  @ImmutableConstructor
  private ResolvedRepo(
      List<SecurityId> securityIds,
      LegalEntityId legalEntityId,
      Currency currency,
      double notional,
      LocalDate startDate,
      LocalDate endDate,
      double yearFraction,
      double rate) {
    JodaBeanUtils.notNull(securityIds, "securityIds");
    JodaBeanUtils.notNull(legalEntityId, "legalEntityId");
    JodaBeanUtils.notNull(currency, "currency");
    JodaBeanUtils.notNull(startDate, "startDate");
    JodaBeanUtils.notNull(endDate, "endDate");
    ArgChecker.inOrderNotEqual(startDate, endDate, "startDate", "endDate");
    ArgChecker.notNegative(yearFraction, "yearFraction");
    JodaBeanUtils.notNull(rate, "rate");
    this.securityIds = ImmutableList.copyOf(securityIds);
    this.legalEntityId = legalEntityId;
    this.currency = currency;
    this.notional = notional;
    this.startDate = startDate;
    this.endDate = endDate;
    this.yearFraction = yearFraction;
    this.rate = rate;
    this.interest = (rate * notional * yearFraction);
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code ResolvedRepo}.
   * @return the meta-bean, not null
   */
  public static ResolvedRepo.Meta meta() {
    return ResolvedRepo.Meta.INSTANCE;
  }

  static {
    MetaBean.register(ResolvedRepo.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static ResolvedRepo.Builder builder() {
    return new ResolvedRepo.Builder();
  }

  @Override
  public ResolvedRepo.Meta metaBean() {
    return ResolvedRepo.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the security identifiers of the collateral.
   * <p>
   * The identifier uniquely identifies the security within the system.
   * @return the value of the property, not null
   */
  public ImmutableList<SecurityId> getSecurityIds() {
    return securityIds;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the legal entity identifier of the collateral.
   * <p>
   * This represents an legal entity associated with the collateral of the repo instrument,
   * e.g., issuer of a fixed coupon bond.
   * @return the value of the property, not null
   */
  public LegalEntityId getLegalEntityId() {
    return legalEntityId;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the primary currency.
   * <p>
   * This is the currency of the principal and the currency that payment is made in.
   * @return the value of the property, not null
   */
  public Currency getCurrency() {
    return currency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the notional amount.
   * <p>
   * It is a positive signed amount if the repo is 'Buy',
   * and a negative signed amount if the repo is 'Sell'.
   * <p>
   * The currency of the notional is specified by {@code currency}.
   * @return the value of the property
   */
  public double getNotional() {
    return notional;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the start date of the repo.
   * <p>
   * This is the first date that interest accrues.
   * <p>
   * This is an adjusted date, which should be a valid business day
   * @return the value of the property, not null
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the end date of the repo.
   * <p>
   * This is the last day that interest accrues.
   * This date must be after the start date.
   * <p>
   * This is an adjusted date, which should be a valid business day
   * @return the value of the property, not null
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the year fraction between the start and end date.
   * <p>
   * The value is usually calculated using a {@link DayCount}.
   * Typically the value will be close to 1 for one year and close to 0.5 for six months.
   * The fraction may be greater than 1, but not less than 0.
   * @return the value of the property
   */
  public double getYearFraction() {
    return yearFraction;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the fixed rate of interest.
   * A 5% rate will be expressed as 0.05.
   * @return the value of the property, not null
   */
  public double getRate() {
    return rate;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ResolvedRepo other = (ResolvedRepo) obj;
      return JodaBeanUtils.equal(securityIds, other.securityIds) &&
          JodaBeanUtils.equal(legalEntityId, other.legalEntityId) &&
          JodaBeanUtils.equal(currency, other.currency) &&
          JodaBeanUtils.equal(notional, other.notional) &&
          JodaBeanUtils.equal(startDate, other.startDate) &&
          JodaBeanUtils.equal(endDate, other.endDate) &&
          JodaBeanUtils.equal(yearFraction, other.yearFraction) &&
          JodaBeanUtils.equal(rate, other.rate);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(securityIds);
    hash = hash * 31 + JodaBeanUtils.hashCode(legalEntityId);
    hash = hash * 31 + JodaBeanUtils.hashCode(currency);
    hash = hash * 31 + JodaBeanUtils.hashCode(notional);
    hash = hash * 31 + JodaBeanUtils.hashCode(startDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(endDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(yearFraction);
    hash = hash * 31 + JodaBeanUtils.hashCode(rate);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(288);
    buf.append("ResolvedRepo{");
    buf.append("securityIds").append('=').append(securityIds).append(',').append(' ');
    buf.append("legalEntityId").append('=').append(legalEntityId).append(',').append(' ');
    buf.append("currency").append('=').append(currency).append(',').append(' ');
    buf.append("notional").append('=').append(notional).append(',').append(' ');
    buf.append("startDate").append('=').append(startDate).append(',').append(' ');
    buf.append("endDate").append('=').append(endDate).append(',').append(' ');
    buf.append("yearFraction").append('=').append(yearFraction).append(',').append(' ');
    buf.append("rate").append('=').append(JodaBeanUtils.toString(rate));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ResolvedRepo}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code securityIds} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<SecurityId>> securityIds = DirectMetaProperty.ofImmutable(
        this, "securityIds", ResolvedRepo.class, (Class) ImmutableList.class);
    /**
     * The meta-property for the {@code legalEntityId} property.
     */
    private final MetaProperty<LegalEntityId> legalEntityId = DirectMetaProperty.ofImmutable(
        this, "legalEntityId", ResolvedRepo.class, LegalEntityId.class);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> currency = DirectMetaProperty.ofImmutable(
        this, "currency", ResolvedRepo.class, Currency.class);
    /**
     * The meta-property for the {@code notional} property.
     */
    private final MetaProperty<Double> notional = DirectMetaProperty.ofImmutable(
        this, "notional", ResolvedRepo.class, Double.TYPE);
    /**
     * The meta-property for the {@code startDate} property.
     */
    private final MetaProperty<LocalDate> startDate = DirectMetaProperty.ofImmutable(
        this, "startDate", ResolvedRepo.class, LocalDate.class);
    /**
     * The meta-property for the {@code endDate} property.
     */
    private final MetaProperty<LocalDate> endDate = DirectMetaProperty.ofImmutable(
        this, "endDate", ResolvedRepo.class, LocalDate.class);
    /**
     * The meta-property for the {@code yearFraction} property.
     */
    private final MetaProperty<Double> yearFraction = DirectMetaProperty.ofImmutable(
        this, "yearFraction", ResolvedRepo.class, Double.TYPE);
    /**
     * The meta-property for the {@code rate} property.
     */
    private final MetaProperty<Double> rate = DirectMetaProperty.ofImmutable(
        this, "rate", ResolvedRepo.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "securityIds",
        "legalEntityId",
        "currency",
        "notional",
        "startDate",
        "endDate",
        "yearFraction",
        "rate");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1550081880:  // securityIds
          return securityIds;
        case 866287159:  // legalEntityId
          return legalEntityId;
        case 575402001:  // currency
          return currency;
        case 1585636160:  // notional
          return notional;
        case -2129778896:  // startDate
          return startDate;
        case -1607727319:  // endDate
          return endDate;
        case -1731780257:  // yearFraction
          return yearFraction;
        case 3493088:  // rate
          return rate;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public ResolvedRepo.Builder builder() {
      return new ResolvedRepo.Builder();
    }

    @Override
    public Class<? extends ResolvedRepo> beanType() {
      return ResolvedRepo.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code securityIds} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<SecurityId>> securityIds() {
      return securityIds;
    }

    /**
     * The meta-property for the {@code legalEntityId} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LegalEntityId> legalEntityId() {
      return legalEntityId;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Currency> currency() {
      return currency;
    }

    /**
     * The meta-property for the {@code notional} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> notional() {
      return notional;
    }

    /**
     * The meta-property for the {@code startDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> startDate() {
      return startDate;
    }

    /**
     * The meta-property for the {@code endDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> endDate() {
      return endDate;
    }

    /**
     * The meta-property for the {@code yearFraction} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> yearFraction() {
      return yearFraction;
    }

    /**
     * The meta-property for the {@code rate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> rate() {
      return rate;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1550081880:  // securityIds
          return ((ResolvedRepo) bean).getSecurityIds();
        case 866287159:  // legalEntityId
          return ((ResolvedRepo) bean).getLegalEntityId();
        case 575402001:  // currency
          return ((ResolvedRepo) bean).getCurrency();
        case 1585636160:  // notional
          return ((ResolvedRepo) bean).getNotional();
        case -2129778896:  // startDate
          return ((ResolvedRepo) bean).getStartDate();
        case -1607727319:  // endDate
          return ((ResolvedRepo) bean).getEndDate();
        case -1731780257:  // yearFraction
          return ((ResolvedRepo) bean).getYearFraction();
        case 3493088:  // rate
          return ((ResolvedRepo) bean).getRate();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code ResolvedRepo}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<ResolvedRepo> {

    private List<SecurityId> securityIds = ImmutableList.of();
    private LegalEntityId legalEntityId;
    private Currency currency;
    private double notional;
    private LocalDate startDate;
    private LocalDate endDate;
    private double yearFraction;
    private double rate;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(ResolvedRepo beanToCopy) {
      this.securityIds = beanToCopy.getSecurityIds();
      this.legalEntityId = beanToCopy.getLegalEntityId();
      this.currency = beanToCopy.getCurrency();
      this.notional = beanToCopy.getNotional();
      this.startDate = beanToCopy.getStartDate();
      this.endDate = beanToCopy.getEndDate();
      this.yearFraction = beanToCopy.getYearFraction();
      this.rate = beanToCopy.getRate();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1550081880:  // securityIds
          return securityIds;
        case 866287159:  // legalEntityId
          return legalEntityId;
        case 575402001:  // currency
          return currency;
        case 1585636160:  // notional
          return notional;
        case -2129778896:  // startDate
          return startDate;
        case -1607727319:  // endDate
          return endDate;
        case -1731780257:  // yearFraction
          return yearFraction;
        case 3493088:  // rate
          return rate;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1550081880:  // securityIds
          this.securityIds = (List<SecurityId>) newValue;
          break;
        case 866287159:  // legalEntityId
          this.legalEntityId = (LegalEntityId) newValue;
          break;
        case 575402001:  // currency
          this.currency = (Currency) newValue;
          break;
        case 1585636160:  // notional
          this.notional = (Double) newValue;
          break;
        case -2129778896:  // startDate
          this.startDate = (LocalDate) newValue;
          break;
        case -1607727319:  // endDate
          this.endDate = (LocalDate) newValue;
          break;
        case -1731780257:  // yearFraction
          this.yearFraction = (Double) newValue;
          break;
        case 3493088:  // rate
          this.rate = (Double) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public ResolvedRepo build() {
      return new ResolvedRepo(
          securityIds,
          legalEntityId,
          currency,
          notional,
          startDate,
          endDate,
          yearFraction,
          rate);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the security identifiers of the collateral.
     * <p>
     * The identifier uniquely identifies the security within the system.
     * @param securityIds  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder securityIds(List<SecurityId> securityIds) {
      JodaBeanUtils.notNull(securityIds, "securityIds");
      this.securityIds = securityIds;
      return this;
    }

    /**
     * Sets the {@code securityIds} property in the builder
     * from an array of objects.
     * @param securityIds  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder securityIds(SecurityId... securityIds) {
      return securityIds(ImmutableList.copyOf(securityIds));
    }

    /**
     * Sets the legal entity identifier of the collateral.
     * <p>
     * This represents an legal entity associated with the collateral of the repo instrument,
     * e.g., issuer of a fixed coupon bond.
     * @param legalEntityId  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder legalEntityId(LegalEntityId legalEntityId) {
      JodaBeanUtils.notNull(legalEntityId, "legalEntityId");
      this.legalEntityId = legalEntityId;
      return this;
    }

    /**
     * Sets the primary currency.
     * <p>
     * This is the currency of the principal and the currency that payment is made in.
     * @param currency  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder currency(Currency currency) {
      JodaBeanUtils.notNull(currency, "currency");
      this.currency = currency;
      return this;
    }

    /**
     * Sets the notional amount.
     * <p>
     * It is a positive signed amount if the repo is 'Buy',
     * and a negative signed amount if the repo is 'Sell'.
     * <p>
     * The currency of the notional is specified by {@code currency}.
     * @param notional  the new value
     * @return this, for chaining, not null
     */
    public Builder notional(double notional) {
      this.notional = notional;
      return this;
    }

    /**
     * Sets the start date of the repo.
     * <p>
     * This is the first date that interest accrues.
     * <p>
     * This is an adjusted date, which should be a valid business day
     * @param startDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder startDate(LocalDate startDate) {
      JodaBeanUtils.notNull(startDate, "startDate");
      this.startDate = startDate;
      return this;
    }

    /**
     * Sets the end date of the repo.
     * <p>
     * This is the last day that interest accrues.
     * This date must be after the start date.
     * <p>
     * This is an adjusted date, which should be a valid business day
     * @param endDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder endDate(LocalDate endDate) {
      JodaBeanUtils.notNull(endDate, "endDate");
      this.endDate = endDate;
      return this;
    }

    /**
     * Sets the year fraction between the start and end date.
     * <p>
     * The value is usually calculated using a {@link DayCount}.
     * Typically the value will be close to 1 for one year and close to 0.5 for six months.
     * The fraction may be greater than 1, but not less than 0.
     * @param yearFraction  the new value
     * @return this, for chaining, not null
     */
    public Builder yearFraction(double yearFraction) {
      ArgChecker.notNegative(yearFraction, "yearFraction");
      this.yearFraction = yearFraction;
      return this;
    }

    /**
     * Sets the fixed rate of interest.
     * A 5% rate will be expressed as 0.05.
     * @param rate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder rate(double rate) {
      JodaBeanUtils.notNull(rate, "rate");
      this.rate = rate;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(288);
      buf.append("ResolvedRepo.Builder{");
      buf.append("securityIds").append('=').append(JodaBeanUtils.toString(securityIds)).append(',').append(' ');
      buf.append("legalEntityId").append('=').append(JodaBeanUtils.toString(legalEntityId)).append(',').append(' ');
      buf.append("currency").append('=').append(JodaBeanUtils.toString(currency)).append(',').append(' ');
      buf.append("notional").append('=').append(JodaBeanUtils.toString(notional)).append(',').append(' ');
      buf.append("startDate").append('=').append(JodaBeanUtils.toString(startDate)).append(',').append(' ');
      buf.append("endDate").append('=').append(JodaBeanUtils.toString(endDate)).append(',').append(' ');
      buf.append("yearFraction").append('=').append(JodaBeanUtils.toString(yearFraction)).append(',').append(' ');
      buf.append("rate").append('=').append(JodaBeanUtils.toString(rate));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------

}
