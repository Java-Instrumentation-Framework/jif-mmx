package edu.mbl.jif.ps.orient.util;

import java.awt.*;
import java.awt.geom.*;


/**
 * A implementation of {@link Stroke} which transforms another Stroke
 * with an {@link AffineTransform} before stroking with it.
 *
 * This class is immutable as long as the underlying stroke is
 * immutable.
 * 
 * My paint-method using it then looks like this:
 * 
 * public void paintComponent(Graphics context) {
    super.paintComponent(context);
    Graphics2D g = (Graphics2D)context.create();

    int height = getHeight();
    int width = getWidth();

    g.scale(width/4.0, height/7.0);

    try {
        g.setStroke(new TransformedStroke(new BasicStroke(2f),
                                          g.getTransform()));
    }
    catch(NoninvertibleTransformException ex) {
        // should not occur if width and height > 0
        ex.printStackTrace();
    }

    g.setColor(Color.BLACK);
    g.draw(new Rectangle( 1, 2, 2, 4));
}
 */
public class TransformedStroke
    implements Stroke
{
    /**
     * To make this serializable without problems.
     */
    private static final long serialVersionUID = 1;

    /**
     * the AffineTransform used to transform the shape before stroking.
     */
    private AffineTransform transform;
    /**
     * The inverse of {@link #transform}, used to transform
     * back after stroking.
     */
    private AffineTransform inverse;

    /**
     * Our base stroke.
     */
    private Stroke stroke;


    /**
     * Creates a TransformedStroke based on another Stroke
     * and an AffineTransform.
     */
    public TransformedStroke(Stroke base, AffineTransform at)
        throws NoninvertibleTransformException
    {
        this.transform = new AffineTransform(at);
        this.inverse = transform.createInverse();
        this.stroke = base;
    }


    /**
     * Strokes the given Shape with this stroke, creating an outline.
     *
     * This outline is distorted by our AffineTransform relative to the
     * outline which would be given by the base stroke, but only in terms
     * of scaling (i.e. thickness of the lines), as translation and rotation
     * are undone after the stroking.
     */
    public Shape createStrokedShape(Shape s) {
        Shape sTrans = transform.createTransformedShape(s);
        Shape sTransStroked = stroke.createStrokedShape(sTrans);
        Shape sStroked = inverse.createTransformedShape(sTransStroked);
        return sStroked;
    }

		/*
		 * Attention: This g.getTransform() is returning the complete transformation of g relative to the device space, not only the transformation applied after the .create(). So, if someone did some scaling before giving the Graphics to my component, this would still draw with a 2-device-pixel width stroke, not 2 pixels of the grapics given to my method. If this would be a problem, use it like this:
		 * public void paintComponent(Graphics context) {
    super.paintComponent(context);
    Graphics2D g = (Graphics2D)context.create();

    AffineTransform trans = new AffineTransform();

    int height = getHeight();
    int width = getWidth();

    trans.scale(width/4.0, height/7.0);
    g.transform(trans);

    try {
        g.setStroke(new TransformedStroke(new BasicStroke(2f),
                                          trans));
    }
    catch(NoninvertibleTransformException ex) {
        // should not occur if width and height > 0
        ex.printStackTrace();
    }

    g.setColor(Color.BLACK);
    g.draw(new Rectangle( 1, 2, 2, 4));
}
		 */
}