/*
 * copied from: https://code.google.com/p/rosjava/source/browse/android_honeycomb_mr2/src/org/ros/android/views/visualization/?repo=android&r=07c00460a3826b976c153ea57353a54a4b275e37
 */

package rosjava_tf_example;

import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import geometry_msgs.TransformStamped;
import tf.tfMessage;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;

public class TransformListener extends AbstractNodeMain {

  private Transformer transformer = new Transformer();
  private Subscriber<tfMessage> tfSubscriber;

  public Transformer getTransformer() {
    return transformer;
  }

  public void setTransformer(Transformer transformer) {
    this.transformer = transformer;
  }

  @Override
  public void onStart(final ConnectedNode node) {
    transformer.setPrefix(GraphName.of(node.getParameterTree().getString("~tf_prefix", "")));
    tfSubscriber = node.newSubscriber(GraphName.of("tf"), tf.tfMessage._TYPE); 
    tfSubscriber.addMessageListener(new MessageListener<tfMessage>() {
      @Override
      public void onNewMessage(tfMessage message) {
        for (TransformStamped transform : message.getTransforms()) {
          transformer.updateTransform(transform);
        }
      }
    });

    // TESTING only
    node.executeCancellableLoop(new CancellableLoop() {

		@Override
		protected void loop() throws InterruptedException {
			geometry_msgs.PoseStamped p = node.getTopicMessageFactory().newFromType(geometry_msgs.PoseStamped._TYPE);
			p.getHeader().setFrameId("/base_link");
			p.getPose().getOrientation().setW(1.0);   // make valid quaternion
			// leaving everything else 0 here

			try {
				getTransformer().transformPose(GraphName.of("/map"), p);
				System.out.println(
						"  x: " + p.getPose().getPosition().getX() +
						", y: " + p.getPose().getPosition().getY() +
						", z: " + p.getPose().getPosition().getZ() +
						" ||| Orientation --- " +
						"  x: " + p.getPose().getOrientation().getX() +
						"  y: " + p.getPose().getOrientation().getY() +
						"  z: " + p.getPose().getOrientation().getZ() +
						"  w: " + p.getPose().getOrientation().getW()
						);
			} catch (java.lang.IllegalStateException e) {
				System.err.println(e);
			}
			Thread.sleep(1000);
		}
	});
  }

  @Override
  public void onShutdown(Node node) {
    tfSubscriber.shutdown();
  }

  @Override
  public GraphName getDefaultNodeName() {
	return GraphName.of("rosjava_tf_example");
  }
}
