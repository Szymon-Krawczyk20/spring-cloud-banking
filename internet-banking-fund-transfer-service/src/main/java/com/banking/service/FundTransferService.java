package com.banking.service;

import com.banking.model.TransactionStatus;
import com.banking.model.dto.FundTransfer;
import com.banking.model.dto.request.FundTransferRequest;
import com.banking.model.dto.response.FundTransferResponse;
import com.banking.model.entity.FundTransferEntity;
import com.banking.model.mapper.FundTransferMapper;
import com.banking.model.repository.FundTransferRepository;
import com.banking.service.rest.client.BankingCoreFeignClient;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundTransferService {

    private final FundTransferRepository fundTransferRepository;
    private final BankingCoreFeignClient bankingCoreFeignClient;

    private FundTransferMapper mapper = new FundTransferMapper();

    public FundTransferResponse fundTransfer(FundTransferRequest request) {
        log.info("Sending fund transfer request {}" + request.toString());

        FundTransferEntity entity = new FundTransferEntity();
        BeanUtils.copyProperties(request, entity);
        entity.setStatus(TransactionStatus.PENDING);
        FundTransferEntity optFundTransfer = fundTransferRepository.save(entity);

        FundTransferResponse fundTransferResponse = bankingCoreFeignClient.fundTransfer(request);
        optFundTransfer.setTransactionReference(fundTransferResponse.getTransactionId());
        optFundTransfer.setStatus(TransactionStatus.SUCCESS);
        fundTransferRepository.save(optFundTransfer);

        fundTransferResponse.setMessage("Fund Transfer Successfully Completed");
        return fundTransferResponse;

    }

    public List<FundTransfer> readAllTransfers(Pageable pageable) {
        return mapper.convertToDtoList(fundTransferRepository.findAll(pageable).getContent());
    }
}
