package physics2d;



import jade.GameObject;
import jade.Transform;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;

public class Physics2D {
    // Vec2 is JBOX's version of vectors
    private Vec2 gravity = new Vec2(0, -10);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    // go 1/60th of a second for 60fps
    private float physicsTimeStep = 1.0f / 60.0f;
    /*
    There are two phases in the constraint solver: a velocity phase and a position phase.
    In the velocity phase the solver computes the impulses necessary for the bodies to move correctly.
    In the position phase the solver adjusts the positions of the bodies to reduce overlap and joint detachment. Each phase has its own iteration count.
    In addition, the position phase may exit iterations early if the errors are small.
     */
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public void add(GameObject go) {
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        // Only if Body exists and doesn't already have a rawbody
        if (rb != null && rb.getRawBody() == null) {
            Transform transform = go.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            // "Continuous Collision" Defines whether or not objects can phase past eachother if moving fast enough
            bodyDef.bullet = rb.isContinuousCollision();

            switch (rb.getBodyType()) {
                case Kinematic:
                    bodyDef.type = BodyType.KINEMATIC;
                    break;
                case Static:
                    bodyDef.type = BodyType.STATIC;
                    break;
                case Dynamic:
                    bodyDef.type = BodyType.DYNAMIC;
                    break;
            }

            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            Box2DCollider boxCollider;

            // Sees if it has a circlecollider or not
            if ((circleCollider = go.getComponent(CircleCollider.class)) != null) {
                shape.setRadius(circleCollider.getRadius());

            } else if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
                Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = new Vector2f(boxCollider.getOrigin());
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            Body body = this.world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass());
        }
    }

    // Removes the gameobject from the physics world
    public void destroyGameObject(GameObject go) {
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        // Check to make sure that the rigid body exists before attempting to get it
        if (rb != null) {
            if (rb.getRawBody() != null) {
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }
    public void update(float dt) {
        // Make it so that the physics will only update after 1/60th of a second has passed
        // If a frame is shorter, don't update
        // This means that the physics will skip frames occasionally but delinks the physics from framerate
        physicsTime += dt;
        if (physicsTime >= 0) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

}
