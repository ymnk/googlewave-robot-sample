package com.jcraft.wave_bot.snippet

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.util.Helpers
import Helpers._

import _root_.scala.collection.mutable.Set
import _root_.scala.collection.jcl.Conversions.convertList
import _root_.com.google.wave.api.{Wavelet => GWavelet}

class Wavelet {
  def render(in: NodeSeq): NodeSeq =
    Helpers.bind("wavelets", in, 
                 "list" -> <ul>{Wavelet().map(list _)}</ul>)

  private def list(w:GWavelet) = {

    /**
     * If w is a public wave, we will list its participants.
     */ 
    val participants = w.getParticipants.find(_ == "public@a.gwave.com").map{_ =>
      <ul>{w.getParticipants.map(p => <li>{participant(p)}</li>)}</ul>
    } getOrElse(<span />)

    <li>
     {w.getTitle + "(%s)".format(w.getWaveId)}
     {participants}
    </li>
  }


  /**
   * If p is a bot, it will be converted to an anchor tag.
   */ 
  private def participant(p:String) = if(p.endsWith("@appspot.com")){
    val name = p.substring(0, p.length - "@appspot.com".length)
    <a href={"http://"+name+".appspot.com"} target="blank">{p}</a>
  }
  else p
}

object Wavelet{
  private val wavelets = Set.empty[GWavelet]
  def +=(w:GWavelet):Unit = wavelets + w
  def apply() = wavelets
}
