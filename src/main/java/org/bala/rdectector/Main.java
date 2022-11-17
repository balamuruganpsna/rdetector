package org.bala.rdectector;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    DeploymentNode deploymentNode;
    ArrayList<DeploymentNode> deploymentNodelist= new ArrayList<>();
    Set<String> s = new HashSet<>();

    private static final String API_EXCEPTION = "ApiException happened while fetching list of pods from kubernetes cluster.\"+\"Message: \"";

    private static final String IO_EXCEPTION = "\"IOException happened while fetching data from kubernetes cluster.\"+\"Message: \"";

    private static final String GENERIC_EXCEPTION = "\"Generic Exception happened!.\"+\"Message: \"";
    private static final String POD_NULL = "pod and its associated objects/specifications are null.";

    private static final String POD_FSDN = "Exception happened in fetchAndStoreDeploymentAndNodeName";


    public static void main(String[] args) throws Exception {

        Main m = new Main();
        //Logging configuration
        BasicConfigurator.configure();

        try {

            ApiClient client = Config.defaultClient();
            CoreV1Api api = new CoreV1Api(client);

            //Fetching all pods
            V1PodList v1PodList = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, 10, false);
            v1PodList.getItems().stream().forEach(pod -> {
                try {
                    m.fetchAndStoreDeploymentAndNodeName(pod);
                } catch (Exception e) {
                    throw new RuntimeException("Runtime  Exception while invoking fetchAndStoreDeploymentAndNodeName");
                }
            });
            m.determineMoreDeploymentsInSingleNode();
            logger.info("Successfully Launched");

        }
        catch(ApiException e)
        {
            logger.error(Main.API_EXCEPTION+e.getMessage()+" "+"Cause: "+e.getCause());
            throw new ApiException(500,Main.API_EXCEPTION+e.getMessage());
        }
        catch(IOException e)
        {
            logger.error(Main.IO_EXCEPTION+e.getMessage()+" "+"Cause: "+e.getCause());
            throw new IOException(Main.IO_EXCEPTION+e.getMessage(),e.getCause());
        }
        catch(Exception e)
        {
            logger.error(Main.GENERIC_EXCEPTION+e.getMessage()+" "+e.getCause());
            throw new Exception(Main.GENERIC_EXCEPTION+e.getMessage(),e.getCause());
        }
     }

    public void  fetchAndStoreDeploymentAndNodeName(V1Pod pod) throws Exception {

        try {

            if(pod != null &&
                    ObjectUtils.isNotEmpty(pod) &&
                    pod.getMetadata() != null &&
                    ObjectUtils.isNotEmpty(pod.getMetadata()) &&
                    pod.getMetadata().getLabels() != null) {

                //Fetching Deployment name from pod metadata
                Map<String, String> labels = pod.getMetadata().getLabels();

                for (Map.Entry<String, String> map : labels.entrySet()) {
                    //Only k8s-app key contains the actual deployment name and excluded native kubernetes k8s-app values
                    if (map.getKey().equalsIgnoreCase("k8s-app") &&
                            map.getValue() != null &&
                            ObjectUtils.isNotEmpty(pod.getSpec()) && pod.getSpec().getNodeName() != null &&
                            (!(map.getValue().equals("kube-dns")
                                    || map.getValue().equals("kube-proxy")
                                    || map.getValue().equals("dashboard-metrics-scraper")
                                    || map.getValue().equals("kubernetes-dashboard")))) {

                        //create object to tie up deployment and nodename together
                        deploymentNode = new DeploymentNode(map.getValue(), pod.getSpec().getNodeName());
                        deploymentNodelist.add(deploymentNode);

                    }


                }

            } else {
                logger.error(Main.POD_NULL);
                throw new Exception(Main.POD_NULL);

            }

        }
        catch(Exception e)
        {
            logger.error(Main.POD_FSDN);
            throw new Exception(Main.POD_FSDN+e.getMessage());
        }

        }


    public void determineMoreDeploymentsInSingleNode() {
//        String deploymentMoreThanOne;

            Object[] objects = deploymentNodelist.toArray();

            for (int i = 0; i <objects.length ; i++) {
                for (int j = i+1; j < objects.length ; j++) {

                    boolean equals = objects[i].equals(objects[j]);
                    if(equals)
                    {
                        DeploymentNode object = (DeploymentNode) objects[i];
                        s.add(object.getDeploymentName());
                    }
                }

            }
        //remove duplicates using set
        for (String setString: s) {
            System.out.println(setString);
        }

        }

    }
