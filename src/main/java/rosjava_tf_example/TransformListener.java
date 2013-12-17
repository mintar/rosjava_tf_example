/*
 * copied from: https://code.google.com/p/rosjava/source/browse/android_honeycomb_mr2/src/org/ros/android/views/visualization/?repo=android&r=07c00460a3826b976c153ea57353a54a4b275e37
 */

package rosjava_tf_example;

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
  public void onStart(ConnectedNode node) {
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
