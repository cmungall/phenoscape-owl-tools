package org.phenoscape.owl.mod.zfin

import java.io.File
import scala.collection.JavaConversions._
import scala.collection.TraversableOnce.flattenTraversableOnce
import scala.collection.mutable
import scala.io.Source
import org.apache.commons.lang3.StringUtils
import org.phenoscape.owl.OWLTask
import org.phenoscape.owl.Vocab._
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary
import org.semanticweb.owlapi.apibinding.OWLManager

object ZFINGeneticMarkersToOWL extends OWLTask {

  val manager = OWLManager.createOWLOntologyManager()
  val rdfsLabel = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI())
  val hasExactSynonym = factory.getOWLAnnotationProperty(HAS_EXACT_SYNONYM)

  def convert(markersData: Source): Set[OWLAxiom] = {
    markersData.getLines.map(translate(_)).flatten.toSet[OWLAxiom]

  }

  def translate(line: String): Set[OWLAxiom] = {
    val items = line.split("\t")
    val axioms = mutable.Set[OWLAxiom]()
    if (items(3) != "GENE") {
      axioms.toSet
    } else {
      val geneID = StringUtils.stripToNull(items(0))
      val geneSymbol = StringUtils.stripToNull(items(1))
      val geneFullName = StringUtils.stripToNull(items(2))
      val geneIRI = IRI.create("http://zfin.org/" + geneID)
      val gene = factory.getOWLNamedIndividual(geneIRI)
      axioms.add(factory.getOWLDeclarationAxiom(gene))
      axioms.add(factory.getOWLClassAssertionAxiom(Gene, gene))
      axioms.add(factory.getOWLAnnotationAssertionAxiom(rdfsLabel, geneIRI, factory.getOWLLiteral(geneSymbol)))
      axioms.add(factory.getOWLAnnotationAssertionAxiom(hasExactSynonym, geneIRI, factory.getOWLLiteral(geneFullName)))
      axioms.toSet
    }
  }

}