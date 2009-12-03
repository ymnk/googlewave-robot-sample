package com.jcraft.wave_bot.servlet

import com.google.wave.api._
import scala.collection.jcl.Conversions.convertList
import _root_.com.jcraft.wave_bot.snippet.{Wavelet => MyWavelet}

 
class MyRobotServlet extends AbstractRobotServlet {

  // com.google.wave.api.Event#getParticipants will return java.util.Collection[String]
  implicit def c2i[A](c:java.util.Collection[A]):Iterator[A] = new Iterator[A]{
    val iterator = c.iterator
    def next():A = iterator.next
    def hasNext:Boolean = iterator.hasNext
  }
 
  override def processEvents(bundle:RobotMessageBundle) {
    val wavelet = bundle.getWavelet

    MyWavelet += wavelet 
 
    if (bundle.wasSelfAdded) {
      val blip = wavelet.appendBlip
      val textView = blip.getDocument
      textView.append("Hello, I'm a test robot!")
    }

    import EventType._
    bundle.getEvents foreach { e => e.getType match{
      case WAVELET_PARTICIPANTS_CHANGED =>
        e.getAddedParticipants .
          filter (!_.endsWith("appspot.com")) .  // skip robots
          foreach { participantName =>
            val blip = wavelet.appendBlip
            val textView = blip.getDocument
            textView.append("Welcome, " + participantName + "!")
        }

      case BLIP_SUBMITTED =>
        // If it is modified by robots, we will ignore this change.
        if(!e.getBlip.getContributors.exists(_.endsWith("appspot.com"))){ 

          val submittedText = e.getBlip.getDocument.getText
          val blip = e.getBlip.createChild
          val textView = blip.getDocument
          val creatorName = e.getBlip.getCreator
          textView.append(creatorName + " said '" + submittedText + "'")

          /**
           * val image = new Image("http://example.com/foo.jpg", width, hehgit, comment)
           * e.getBlip.getDocument.appendElement(image)
	   */

          /**
 	   * val gadget = new Gadget("http://example.com/gadget.xml")
	   * e.getBlip.getDocument.appendElement(gadget)
	   */ 
	}

      case et =>
    }}
  }
}
