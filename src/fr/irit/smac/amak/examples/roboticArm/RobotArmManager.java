package fr.irit.smac.amak.examples.roboticArm;


import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.utils.Pair;
import java.util.ArrayList;
import java.util.OptionalDouble;

public class RobotArmManager {

    int jointsNb;

    double[] l;
    double[] joints;

    RobotController controller;

    double xPos;
    double yPos;
    double[] poseGoal;
    double[] goalAngles;


    int experimentCycles;
    double goalErrors;
    ArrayList<Double> allGoalErrors ;
    public OptionalDouble averageError;
    Double errorDispersion;



    public double maxError;
    public int errorRequests = 0;

    public boolean stopExperiment = false;

    public RobotArmManager(int jointsNumber, double[] jointDistances, RobotController robotController, int experimentCyclesCyclesLimit){

        jointsNb = jointsNumber;
        l = jointDistances;
        controller = robotController;
        poseGoal = new double[2];

        poseGoal[0] = 0.0;
        poseGoal[1] = 0.0;

        experimentCycles = experimentCyclesCyclesLimit;
        goalErrors = 0.0;
        allGoalErrors = new ArrayList<>();
    }

    public double[] forwardKinematics(double[] jointsAngles, int joint){


        double[] position = new double[3];
        joints = jointsAngles;

        double[][] T = TRZ(0,1) ;
        int i = 2;
        while (i<=joint){

            T = product(T,TRZ(i-1,i));
            i++;
        }

        position[0] = T[0][3];
        position[1] = T[1][3];
        position[2] = T[2][3];


        return position;
    }


    private double[][] TRZ(int i_1, int i){
        double[][] transformationMatrix = new double[4][4];
        for(int j=0;j<4;j++) {
            for (int k = 0; k < 4; k++) {
                transformationMatrix[j][k]=0.0;
            }
        }
        transformationMatrix[0][0] = Math.cos(joints[i_1]);
        transformationMatrix[1][0] = Math.sin(joints[i_1]);
        transformationMatrix[0][1] = -Math.sin(joints[i_1]);
        transformationMatrix[1][1] = Math.cos(joints[i_1]);
        transformationMatrix[0][3] = l[i_1]*Math.cos(joints[i_1]);
        transformationMatrix[1][3] = l[i_1]*Math.sin(joints[i_1]);
        transformationMatrix[2][2] = 1;
        transformationMatrix[3][3] = 1;


        return transformationMatrix;
    }

    private double[][] product(double[][] m1, double[][] m2){

        double[][] prodcutResult = new double[4][4];

        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                prodcutResult[i][j]=0;
                for(int k=0;k<4;k++)
                {
                    prodcutResult[i][j]+=m1[i][k]*m2[k][j];
                }
            }
        }

        return prodcutResult;

    }






    public Pair<Pair<Double,Double>[],Pair<Double,Double>[]> decideAndAct(int cycle, double[] anglesBase, double[] angles){

        xPos = 0.0;
        yPos = 0.0;
        Pair<Double,Double>[] starts = new Pair[jointsNb];
        Pair<Double,Double>[]  ends = new Pair[jointsNb];


        if(cycle<=ROBOT_PARAMS.numberOfCyclesForTheExperiment){

            setJoints(cycle, anglesBase, angles, starts, ends);

            if(cycle%50==0){
                setNewGoal();
            }

            calculateErrorsFromGoal(cycle, ends[jointsNb - 1]);

        }else{
            endOfExperiment(angles, starts, ends);
        }

        return new Pair<>(starts, ends);
    }

    private void setJoints(int cycle, double[] anglesBase, double[] angles, Pair<Double, Double>[] starts, Pair<Double, Double>[] ends) {
        for (int i = 0;i<jointsNb;i++){

            //controller.setSinusoidalJoints(i, cycle, anglesBase, angles);
            controller.setRandomJoint(i, angles);

            starts[i] = new Pair<>(xPos,yPos);
            double[] position = forwardKinematics(angles,i+1);
            xPos = position[0];
            yPos = position[1];
            ends[i] = new Pair<>(xPos,yPos);

        }
    }

    private void endOfExperiment(double[] angles, Pair<Double, Double>[] starts, Pair<Double, Double>[] ends) {

        stopExperiment = true;

        for (int i = 0;i<jointsNb;i++){

            controller.setSingleJoint(i, angles, 0.0);
            starts[i] = new Pair<>(xPos,yPos);
            double[] position = forwardKinematics(angles,i+1);
            xPos = position[0];
            yPos = position[1];
            ends[i] = new Pair<>(xPos,yPos);

        }


    }

    private void calculateErrorsFromGoal(int cycle, Pair<Double, Double> end) {
        double currentError ;
        currentError = Math.sqrt( Math.pow(poseGoal[0]- end.getA(),2) +  Math.pow(poseGoal[1]- end.getB(),2))/maxError;

        System.out.println("ERROR " + currentError + " [" + cycle + "]");
        allGoalErrors.add(new Double(currentError));


        if(cycle == experimentCycles){

            averageError = allGoalErrors.stream().mapToDouble(a->a).average();
            errorDispersion = allGoalErrors.stream().mapToDouble(a->Math.pow((a-averageError.getAsDouble()),2)).sum();

            System.out.println("AVERAGE ERROR " + averageError.getAsDouble() + " [" + ROBOT_PARAMS.numberOfCyclesForTheExperiment + " cycles]");
        }
    }

    private void setNewGoal() {
        double randomAngle = Math.random()*Math.PI*2;
        double randomRadius;

        if(jointsNb ==1){
            randomRadius = 200;
        }else{
            randomRadius = Math.random()*200;
        }


        poseGoal[0] = randomRadius*Math.cos(randomAngle);
        poseGoal[1] = randomRadius*Math.sin(randomAngle);
    }

    public double[] getGoal(){
        return poseGoal;
    }



    private double angleConvertionForLearning(double value){
        double multilpicator = 100;//*jointsNb/2;
        if(value<Math.PI){
            return ((2*Math.PI) + value)*multilpicator;
        }else{
            return value*multilpicator;
        }


    }

    private double angleConvertionForRequest(double value){
        double multilpicator = 100;//*jointsNb/2;
        if(value/multilpicator>2*Math.PI){
            if(value/multilpicator>3*Math.PI){
                //System.out.println(value/multilpicator);
                errorRequests++;
                return Math.PI;
            }else{
                return controller.modulo2PI(value/multilpicator);
            }
        }else{
            if(value/multilpicator<Math.PI){
                //System.out.println(value/multilpicator);
                errorRequests++;
                return  Math.PI;
            }else{
                return value/multilpicator;
            }

        }
        //return value/multilpicator;
    }

}
