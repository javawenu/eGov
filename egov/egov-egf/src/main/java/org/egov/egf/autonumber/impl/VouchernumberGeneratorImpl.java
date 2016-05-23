package org.egov.egf.autonumber.impl;

import java.io.Serializable;

import org.egov.commons.CFinancialYear;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.egf.autonumber.VouchernumberGenerator;
import org.egov.infra.persistence.utils.ApplicationSequenceNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class VouchernumberGeneratorImpl implements VouchernumberGenerator {

	@Autowired
	private FinancialYearHibernateDAO financialYearHibernateDAO;
	@Autowired
	private ApplicationSequenceNumberGenerator applicationSequenceNumberGenerator;
/**
 * 
 * Format fundcode/vouchertype/seqnumber/month/financialyear but 
 * sequence is running number for a year
 *
 */
	public String getNextNumber(CVoucherHeader vh) 
	{
		String voucherNumber="";
		
		String sequenceName="";
		CFinancialYear financialYearByDate = financialYearHibernateDAO.getFinancialYearByDate(vh.getVoucherDate());
		sequenceName="seq_"+vh.getFundId().getIdentifier()+"_"+vh.getVoucherNumberPrefix()+"_"+financialYearByDate.getFinYearRange();
		Serializable nextSequence = applicationSequenceNumberGenerator.getNextSequence(sequenceName);
		
		voucherNumber=	String.format("%s%s%08d%s%s", vh.getFundId().getIdentifier(), vh.getVoucherNumberPrefix(),
				nextSequence,vh.getVoucherDate().getMonth(),financialYearByDate.getFinYearRange());
		
		return voucherNumber;
	}
}
