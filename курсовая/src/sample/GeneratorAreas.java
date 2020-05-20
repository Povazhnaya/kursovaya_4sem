package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class GeneratorAreas extends Application {
    public static final Logger LOG = Logger.getLogger(GeneratorAreas.class.getName());

    @Override
    public void start(Stage stage) {
        PropertyConfigurator.configure("log4j.properties");
        //Границы
        Path path = new Path();
        MoveTo moveToV = new MoveTo(50, 50);
        LineTo line1 = new LineTo();
        MoveTo moveToG = new MoveTo(50, 50);
        LineTo line2 = new LineTo();
        for(int k = 0;k < 11;k++){
            if(k==5){
                k++;
            }
            line1 = new LineTo(50+50*k, 550);
            moveToV = new MoveTo(50+50*k, 50);
            path.getElements().add(moveToV);
            path.getElements().addAll(line1);
            line2 = new LineTo(550, 50+50*k);
            moveToG = new MoveTo(50, 50+50*k);
            path.getElements().add(moveToG);
            path.getElements().addAll(line2);

        }
        Line oy =new Line(300,50,300,550);
        oy.setStrokeWidth(3);
        Line ox =new Line(50,300,550,300);
        ox.setStrokeWidth(3);
        //

        Button btn = new Button();

        btn.setText("Create!");
        btn.setMinWidth(600);
        btn.setMaxWidth(600);
        btn.setMinHeight(35);
        btn.setMaxHeight(35);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Line line = new Line();
                Line line2 = new Line();
                Circle circle = new Circle();
                Polygon polygon1 = new Polygon();
                Polygon polygon2 = new Polygon();
                boolean collisionDetected = false;
                while(collisionDetected == false) {
                    try{
                        circle = createCircle();
                        circle.setStroke(Color.GREEN);
                        circle.setFill(Color.BLACK);
                        circle.setStrokeWidth(3);
                        //circle.setStrokeType(StrokeType.INSIDE);
                        line = createLine();
                        line.setStroke(Color.BLUE);
                        line.setStrokeWidth(3);
                        line2 = createLine();
                        line2.setStroke(Color.RED);
                        line2.setStrokeWidth(3);
                    }
                    catch (Exception ex){
                        LOG.warn("can't make object");
                    }
                    polygon1 = new Polygon();
                    polygon1.getPoints().addAll(new Double[]{
                            line.getStartX(), 50.0,
                            550.0, 50.0,
                            550.0, 550.0,
                            line.getEndX(), 550.0,
                    });

                    polygon2 = new Polygon();
                    polygon2.getPoints().addAll(new Double[]{
                            line2.getStartX(), 50.0,
                            550.0, 50.0,
                            550.0, 550.0,
                            line2.getEndX(), 550.0,
                    });

                    Shape intersect = Shape.intersect(circle, line);
                    Shape intersect2 = Shape.intersect(circle, line2);
                    if (intersect.getBoundsInLocal().getWidth() != -1 && intersect2.getBoundsInLocal().getWidth() != -1 ) {
                        collisionDetected = true;
                    }
                    System.out.println(collisionDetected);
                };
                double a1,b1,a2,b2,d,c1,c2,xi,yi=0;
                a1 = line.getStartY() - line.getEndY();
                b1 = line.getEndX() - line.getStartX();
                a2 = line2.getStartY() - line2.getEndY();
                b2 = line2.getEndX() - line2.getStartX();
                d = a1 * b2 - a2 * b1;
                if ( d != 0 ) { c1 = line.getEndY() * line.getStartX() - line.getEndX() * line.getStartY();
                    c2 = line2.getEndY() * line2.getStartX() - line2.getEndX() * line2.getStartY();

                    xi = (b1 * c2 - b2 * c1) / d;
                    yi = (a2 * c1 - a1 * c2) / d;
                }



                Shape shape = Shape.subtract(polygon1,polygon2);
                if(((line2.getStartX()<=line.getStartX() || line2.getEndX()<=line.getEndX()) &&
                        ((line2.getStartX()-line.getStartX()>=0 && circle.getCenterY() >= yi)
                                || (line2.getEndX()-line.getEndX()>=0 && circle.getCenterY() <= yi)))
                        || (line2.getStartX()<=line.getStartX() && line2.getEndX()<=line.getEndX()) ){
                    shape = Shape.subtract(polygon2,polygon1);
                }

                Shape shape1 = Shape.subtract(circle,shape);

                shape1.setFill(Color.WHITE);
                shape1.setStrokeWidth(3);
                shape1.setStroke(Color.GREEN);

                try(FileWriter writer = new FileWriter("result.txt", false))
                {
                    // запись всей строки
                    String text = "1 yr : " + (line.getStartY() - line.getEndY()) + "*x + " + (line.getEndX() - line.getStartX()) + "*y + " + (line.getEndY() * line.getStartX() - line.getEndX() * line.getStartY()) + " = 0\n";
                    writer.write(text);
                    text = "2 yr : " + (line2.getStartY() - line2.getEndY()) + "*x + " + (line2.getEndX() - line2.getStartX()) + "*y + " + (line2.getEndY() * line2.getStartX() - line2.getEndX() * line2.getStartY()) + " = 0\n";
                    writer.write(text);
                    text = "Yr kryga : (x - " + circle.getCenterX() + ")^2 + (y - " + circle.getCenterY() + ")^2 = " + circle.getRadius() + "^2\n";
                    writer.write(text);
                }
                catch(IOException ex){
                    LOG.warn("can't write at file");
                    System.out.println(ex.getMessage());
                }
                //Creating a Group object
                Group root = new Group(circle,shape1,path,oy,ox,line,line2,btn);
                //Creating a scene object
                Scene scene = new Scene(root, 600, 600);
                //Setting title to the Stage
                stage.setTitle("Generator of various areas");
                //Adding scene to the stage
                stage.setScene(scene);
                stage.setResizable(false);
                //Displaying the contents of the stage
                stage.show();
                LOG.info("all is good");
            }
        });

        //Creating a Group object
        Group root = new Group(path,btn,oy,ox);
        //Creating a scene object
        Scene scene = new Scene(root, 600, 600);
        //Setting title to the Stage
        stage.setTitle("Generator of various areas");
        //Adding scene to the stage
        stage.setScene(scene);
        stage.setResizable(false);
        //Displaying the contents of the stage
        stage.show();
        LOG.info("all is good");
    }

    public Line createLine(){
        Line line = new Line();
        double a = 50 + (int) ( Math.random() * (500+1) );
        line.setStartX(a);
        //a = 50 + (int) ( Math.random() * (500+1) );
        line.setStartY(50);
        a = 50 + (int) ( Math.random() * (500+1) );
        line.setEndX(a);
        //a = 50 + (int) ( Math.random() * (500+1) );
        line.setEndY(550);
        return line;
    }

    public Circle createCircle(){
        Circle circle = new Circle();
        double a = 50 + Math.random() * (75);
        circle.setRadius(a);
        double b = 175 + Math.random() * (250);
        circle.setCenterX(b);
        double c = 175 + Math.random() * (250);
        circle.setCenterY(c);
        return circle;
    }

    public static void main(String args[]){
        launch(args);
    }

}