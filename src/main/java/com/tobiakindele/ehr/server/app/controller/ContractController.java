package com.tobiakindele.ehr.server.app.controller;

import com.owlike.genson.Genson;
import com.tobiakindele.ehr.server.app.enums.TransactionType;
import com.tobiakindele.ehr.server.app.service.ContractService;
import org.hyperledger.fabric.gateway.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value = "/api")
public class ContractController {

    private final static Logger logger = LoggerFactory.getLogger(ContractController.class);

    private final ContractService contractService;

    private final Genson genson = new Genson();

    public ContractController(final ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping(value = "/ehrdata", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Object getAllEHRData() throws ContractException, InterruptedException, TimeoutException {
        return contractService.invokeTransaction("GetAllEHRData", TransactionType.EVALUATE);
    }

    @GetMapping(value = "/ehrdata/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Object getEHRData(@PathVariable String id) throws ContractException, InterruptedException, TimeoutException {
        return contractService.invokeTransaction("ReadEHRData", TransactionType.EVALUATE, id);
    }

    @PostMapping(value = "/ehrdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Object createEHRData(@RequestBody List<Object> requestBody) throws ContractException, InterruptedException, TimeoutException {
        String payload = genson.serialize(requestBody);
        logger.info(payload);
        return contractService
                .invokeTransaction(
                        "CreateEHRData",
                        TransactionType.SUBMIT,
                        payload);
    }
}
