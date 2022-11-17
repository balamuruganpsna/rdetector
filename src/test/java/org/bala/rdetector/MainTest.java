package org.bala.rdetector;

import io.kubernetes.client.openapi.models.V1Pod;
import org.bala.rdectector.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class MainTest {

        @InjectMocks
        Main m;

        @InjectMocks
        V1Pod pod;


   @Test
    public void ExceptionTestInfetchAndStoreDeploymentAndNodeNameMethodByPassingNullObject() throws Exception {
       Assertions.assertThrows(Exception.class,() ->
       {
           m.fetchAndStoreDeploymentAndNodeName(pod);
       });
    }


}
