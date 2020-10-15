package fr.irit.smac.amak.examples.roboticArm;



public class RobotController {

    int jointsNumber;

    public RobotController(int jointsNb){
        jointsNumber = jointsNb;
    }

    public void setSinusoidalJoints(int jointIndice, int cycle, double[] anglesBase, double[] angles) {

        setContinuousSinusoidJoints(jointIndice, cycle, anglesBase, angles);

    }

    public void setRandomJoint(int jointIndice, double[] angles) {

        setRandomJoints(jointIndice, angles);

    }

    private void setContinuousSinusoidJoints(int jointIndice, double cycle, double[] anglesBase, double[] angles) {
        anglesBase[jointIndice] = 10*Math.sin(0.0001* cycle * Math.PI );
        if (jointIndice == 0) angles[jointIndice] = anglesBase[jointIndice] + (7.0/(double)jointsNumber)*Math.sin((0.01)* cycle * Math.PI    +  ((jointsNumber-jointIndice)*0.5) );
        else angles[jointIndice] = (7.0/(double)jointsNumber) * Math.sin(0.01* cycle * Math.PI    +  ((jointsNumber-jointIndice)*0.5) );

        angles[jointIndice] = modulo2PI(angles[jointIndice]);
    }

    private void setRandomJoints(int jointIndice, double[] angles) {

        if(jointIndice==0){
            angles[jointIndice] = Math.random()* 2 * Math.PI;
        }else{
            //double dispersion = Math.PI/2;
            //double dispersion = -jointsNumber*Math.PI/240 + 7*Math.PI/24; //Math.PI/6 for 30 joints and Math.PI/4 for 10 joints
            double dispersion = 2.5593*Math.pow(jointsNumber,-0.479); //Math.PI/6 for 30 joints, Math.PI/4 for 10 joints , Math.PI/2 for 3 joints
            angles[jointIndice] = modulo2PI(gaussianRandom(0.0,dispersion));
        }


    }

    public void setSingleJoint(int jointIndice, double[] angles, double value) {
        angles[jointIndice] = maxMin2PI(value);
    }


    private double gaussianRandom(double mean, double dispersion){
        java.util.Random r = new java.util.Random();
        return  (r.nextGaussian() * dispersion) + mean;
    }

    private double addConstrains(double angleValue, double limit){
        if (Math.PI - limit < angleValue && angleValue < Math.PI){
            return Math.PI - limit;
        }
        else if (Math.PI  < angleValue && angleValue < Math.PI + limit){
            return Math.PI + limit;
        }
        else{
            return angleValue;
        }
    }

    public void setJointsFromRequest(double[] currentAngles, double[] goalAngles){

        for(int i=0;i<jointsNumber;i++){

            currentAngles[i] = maxMin2PI( goalAngles[i]);

        }

    }

    public double maxMin2PI(double angle){

        if(angle<0.0){
            return 0.0;
        }else if(angle>Math.PI*2){
            return Math.PI*2;
        }else{
            return angle;
        }

    }



    public double modulo2PI(double angle){
        double newAngle = angle;
        if(newAngle> 2* Math.PI){
            while(newAngle > 2* Math.PI){
                newAngle -= 2* Math.PI;
            }

        }else if(newAngle < 0){
            while(newAngle <0){
                newAngle += 2* Math.PI;
            }
        }

        return newAngle;
    }

}
