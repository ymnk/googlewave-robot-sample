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
    val (title, participants) = 
      w.getParticipants.find (_ == "public@a.gwave.com").map{_ =>
      val url = "https://wave.google.com/wave/#restored:search,restored:wave:"+
                w.getWaveId.replace("!", "%21").replace("+", "%252B")
      (<a href={url}>{w.getTitle}</a>,
       <ul>{w.getParticipants.map(p => <li>{participant(p)}</li>)}</ul>)
    } getOrElse((<span>{w.getTitle}</span>, <span />))

    <li>
     {title}
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
  def +=(w:GWavelet):Unit = {
    if(!wavelets.exists(_.getWaveId==w.getWaveId)){
      wavelets + w
    } 
  }
  def apply() = wavelets
}
