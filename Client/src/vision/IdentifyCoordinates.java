package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;
import static org.bytedeco.opencv.global.opencv_imgproc.circle;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.line;
import static org.bytedeco.opencv.global.opencv_imgproc.medianBlur;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.opencv.core.CvType;

public class IdentifyCoordinates {

	

	public int[] getWallCorners(Mat picture)
	{ {
		int frameWidth  = picture.cols();
		int frameHeight = picture.rows();
		int center[] = {frameWidth/2,frameHeight/2};

		int[][] upperLeftBoundary  = { {0,0},{center[0],0},{} };
		int[][] upperRightBoundary = { {center[0],0} };
		int[][] lowerLeftBoundary  = { {center[0],center[1]} };
		int[][] lowerRightBoundary = {  {0,center[1]}      };

		int[][] coords = new int[4][2];

		return center;

	//	Mat color_map = extractColor(picture, "blue");
	//	BytePointer p = color_map.data();

    }


	}





	public int[][] getEdgesTriangle(Mat picture) {
		// Dimensions of frame
		int frameWidth  = picture.cols();
		int frameHeight = picture.rows();

	public int[][] getCirleCoordinates(Mat picture) {

		int[][] coords = new int[4][2];

		Mat extractedMat = extractColor(picture, "blue");
		medianBlur(extractedMat, extractedMat, 9);

		//BytePointer p = extractedMat.data();

		Vec3fVector circles = new Vec3fVector();
		findCircles(picture, circles);

		for(int i=0; i<circles.size(); i++) {
			circle(picture, new Point((int) circles.get(i).get(0), (int) circles.get(i).get(1)), (int) circles.get(i).get(2), Scalar.RED);
			System.out.println("Cicles: "+circles.get(i).get(0)+"\t"+circles.get(i).get(1));
		}



		return coords;



	}

	public int[][] getEdgesSqaure(Mat picture){

		int[][] coords = new int[4][2];

		// Dimensions of frame
		int frameWidth  = picture.cols();
		int frameHeight = picture.rows();

		// Center of frame
		int[] center = { (int) frameWidth/2, (int) frameHeight/2 };

		// Coordinates of adjustment-box
		int[] upperLeftBoundary  = { center[0]-100, center[1]+100 };
		int[] upperRightBoundary = { center[0]+100, center[1]+100 };
		int[] lowerLeftBoundary  = { center[0]-100, center[1]-100 };
		int[] lowerRightBoundary = { center[0]+100, center[1]-100 };

		Mat color_map = extractColor(picture, "red");
		BytePointer p = color_map.data();


		d21:
			for( int y = lowerLeftBoundary[1]; y <= upperLeftBoundary[1]; y++ )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					//System.out.print( p.get((y*color_map.arrayWidth())+x));
					if(p.get((y*color_map.arrayWidth())+x) == -1){

						coords[0][1] = y;
						coords[1][1] = y;

						break d21;
					}
				}
		d22:
			for( int y = upperLeftBoundary[1]; y >= lowerLeftBoundary[1]; y-- )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					if(p.get((y*color_map.arrayWidth())+x) == -1){

						coords[3][1] = y;
						coords[2][1] = y;

						break d22;
					}
				}

				d23:
					for( int x = lowerLeftBoundary[0]; x <= lowerRightBoundary[0]; x++ )
						for( int y = lowerLeftBoundary[1]; y <= upperRightBoundary[1]; y++ ){

							if(p.get((y*color_map.arrayWidth())+x) == -1){

								coords[0][0] = x;
								coords[3][0] = x;

								break d23;
							}
						}

				d24:
					for( int x = lowerRightBoundary[0]; x >= lowerLeftBoundary[0]; x-- )
						for( int y = lowerLeftBoundary[1]; y <= upperRightBoundary[1]; y++ ){

							if(p.get((y*color_map.arrayWidth())+x) == -1){

								coords[1][0] = x;
								coords[2][0] = x;

								break d24;
							}
						}

						return coords;
	}

	public Mat extractColor(Mat picture, String color) {

		//Mat to_out = new Mat();

		int h_min = 0, h_max = 255;
		int s_min = 0, s_max = 255;
		int v_min = 0, v_max = 255;


		// Transform the picture for a more precise calibration
		medianBlur(picture, picture, 9);

		// Prepare for HSV color extraction
		cvtColor(picture, picture, COLOR_BGR2HSV);

		if(color.contentEquals("red")) {
			// Range of red color of cross
			h_min = 0; 		
			h_max = 10;
			s_min = 120;
			s_max = 255;
			v_min = 120;
			v_max = 255;

			/*
			// Range of red color in BGR
			int b_min = 0, 		b_max = 111;
			int g_min = 27, 	g_max = 136;
			int r_min = 151,	r_max = 255;
			 */

		} else if(color.contentEquals("blue")) {
			// Range of red color of cross
			h_min = 105; 		
			h_max = 125;
			s_min = 120;
			s_max = 255;
			v_min = 30;
			v_max = 255;
		}


		// Create Mat's based of the colors for the inRange function
		Mat min_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_min, s_min, v_min, 0));
		Mat max_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_max, s_max, v_max, 0));

		// Remove any other color than in the range of min and max
		opencv_core.inRange(picture, min_Mat, max_Mat, picture);

		return picture;
	}

	public void checkRedundant(int x, int y, int[][] coords) {
		for(int i=0; i<coords.length; i++) {
			if(coords[i][0] == 0) {
				coords[i][0] = x;
				coords[i][1] = y;
			} else {
				if(x == coords[i][0] && y != coords[i][1]) {
					coords[i][0] = x;
					coords[i][1] = y;
				}

				if(x != coords[i][0] && y == coords[i][1]) {
					coords[i][0] = x;
					coords[i][1] = y;
				}
			}
		}
	}


	public void findCircles(Mat picture, Vec3fVector circles) {
		HoughCircles(picture, circles, CV_HOUGH_GRADIENT, 1, 20, 50, 10, 10, 40);
	}

}
