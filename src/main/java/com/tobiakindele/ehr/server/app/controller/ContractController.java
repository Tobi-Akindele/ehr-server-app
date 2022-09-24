package com.tobiakindele.ehr.server.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tobiakindele.ehr.server.app.enums.TransactionType;
import com.tobiakindele.ehr.server.app.service.ContractService;
import com.tobiakindele.ehr.server.app.utils.Query;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContractController(final ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping(value = "/ehrdata", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Object getAllEHRData() throws ContractException, InterruptedException, TimeoutException {
        return contractService.invokeTransaction("GetAllEHRData", TransactionType.EVALUATE);
    }

    @GetMapping(value = "/ehrdata/page", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Object getAllEHRData(
            @RequestHeader(name = "bookmark", defaultValue = "") String bookmark,
            @RequestHeader(name = "pageSize", defaultValue = "10") String pageSize)
            throws ContractException, InterruptedException, TimeoutException, JsonProcessingException {

        Query query = new Query();
        query.setSelector(new Query.Selector(new Query.Not(null)));
        query.setFields(List.of("id",
                "doc",
                "name",
                "fileName",
                "fileType",
                "size"));
        query.setBookmark(bookmark);
        query.setLimit(Integer.parseInt(pageSize));
        String queryString = objectMapper.writeValueAsString(query);

        logger.info(String.format("[+] Query: %s", queryString));

        return contractService.invokeTransaction("GetPaginatedEHRData",
                new String[]{queryString, pageSize, bookmark});
    }

    @GetMapping(value = "/ehrdata/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Object getEHRData(@PathVariable String id) throws ContractException, InterruptedException, TimeoutException {
        return contractService.invokeTransaction("ReadEHRData", TransactionType.EVALUATE, id);
    }

    @PostMapping(value = "/ehrdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Object createEHRData(@RequestBody Object requestBody)
            throws ContractException, InterruptedException, TimeoutException, JsonProcessingException {
        String payload = objectMapper.writeValueAsString(requestBody);
        logger.info(payload);
        return contractService
                .invokeTransaction(
                        "CreateEHRData",
                        TransactionType.SUBMIT,
                        payload);
    }
}
