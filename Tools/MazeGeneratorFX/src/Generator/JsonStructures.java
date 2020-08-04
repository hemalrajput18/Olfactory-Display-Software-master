package Generator;

public class JsonStructures {
    public static class MazeData{
        int width;
        int height;
        double cellWidth;
        double cellHeight;
        double wallFraction;
        String commType;
        String ipAddress;
        int port;

        public MazeData(int width, int height, double cellWidth, double cellHeight, double wallFraction, String commType, String ipAddress, int port){
            this.width = width;
            this.height = height;
            this.cellWidth = cellWidth;
            this.cellHeight = cellHeight;
            this.wallFraction = wallFraction;
            this.commType = commType;
            this.ipAddress = ipAddress;
            this.port = port;
        }
    }

    public static class Point3D<T>{
        T x,y,z;
        public Point3D(T x, T y, T z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString(){
            return x + ", " + y + ", " + z;
        }
    }

    public static class Point2D<T>{
        T x,y;
        public Point2D(T x, T y){
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString(){
            return x + ", " + y;
        }
    }

    /*JSON Data structure*/
    public static class MazeStimuli<T> {
        private String type;
        private Point2D cell;
        private Point3D size;
        private String STName;
        private String ResourcePath;
        private T MetaData;
        private double x_offset = 0.0;
        private double z_offset = 0.0;

        public MazeStimuli(String type, Point2D cell, Point3D size, String ResourcePath, String STName){
            this.type = type;
            this.cell = cell;
            this.size = size;
            this.ResourcePath = ResourcePath;
            this.STName = STName;
        }

        public MazeStimuli(Point2D cell, Point3D size){
            this.cell = cell;
            this.size = size;
        }

        public void setXZOffset(double x, double z){
            x_offset = x;
            z_offset = z;
        }

        public void setResourcePath(String rp){ResourcePath = rp;}

        public String getResourcePath(){return ResourcePath;}

        public String getType(){return type;}

        public void setSTName(String stn){STName = stn;}

        public void setSize(Point3D size){this.size = size;}

        public void setPosition(Point2D point){ cell = point;}

        public void setType(String type){this.type = type;}

        public void setMetaData(T data){
            this.MetaData = data;
        }
    }
}
