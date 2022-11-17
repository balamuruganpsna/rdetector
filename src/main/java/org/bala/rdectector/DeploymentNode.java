package org.bala.rdectector;

public class DeploymentNode {

    public DeploymentNode(String deploymentName, String nodeName) {
        this.deploymentName = deploymentName;
        this.nodeName = nodeName;
    }

    private String deploymentName;
    private String nodeName;


    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public boolean equals(Object obj)
    {

        // checking if the two objects
        // pointing to same object
        if (this == obj)
            return true;

        // checking for two condition:
        // 1) object is pointing to null
        // 2) if the objects belong to
        // same class or not
        if (obj == null
                || this.getClass() != obj.getClass())
            return false;

        DeploymentNode p1 = (DeploymentNode) obj; // type casting object to the
        // intended class type

        // checking if the two
        // objects share all the same values
        return this.deploymentName.equals(p1.deploymentName)
                && this.nodeName.equals(p1.nodeName);

    }
}
