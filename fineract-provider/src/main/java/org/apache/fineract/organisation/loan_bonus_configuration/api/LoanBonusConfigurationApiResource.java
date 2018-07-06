package org.apache.fineract.organisation.loan_bonus_configuration.api;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.organisation.loan_bonus_configuration.data.LoanBonusConfigurationData;
import org.apache.fineract.organisation.loan_bonus_configuration.domain.LoanBonusConfiguration;
import org.apache.fineract.organisation.loan_bonus_configuration.service.LoanBonusConfigurationReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/loan/bonus/configuration")
@Component
@Scope("singleton")
public class LoanBonusConfigurationApiResource {
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<LoanBonusConfigurationData> apiJsonSerializerService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanBonusConfigurationReadPlatformService readPlatformService;

    @Autowired
    public LoanBonusConfigurationApiResource(ApiRequestParameterHelper apiRequestParameterHelper, DefaultToApiJsonSerializer<LoanBonusConfigurationData> apiJsonSerializerService, PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, LoanBonusConfigurationReadPlatformService readPlatformService) {
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.readPlatformService = readPlatformService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveConfig(@Context final UriInfo uriInfo){
        LoanBonusConfigurationData loanBonusConfiguration = this.readPlatformService.getConfiguration();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, loanBonusConfiguration, LoanBonusConfigJsonOutputParameters.getAllValues());
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createConfig(final String jsonRequestBody) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createLoanBonusConfiguration()
                .withJson(jsonRequestBody) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }


    @Path("{id}")
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateConfig(@PathParam("id") final Long id, final String jsonRequestBody) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateLoanBonusConfiguration(id)
                .withJson(jsonRequestBody) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }
}
