package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Gene;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Collection;

/**
 * Intact implementation of Gene
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/14</pre>
 */
@Entity
@DiscriminatorValue( "gene" )
public class IntactGene extends IntactInteractor implements Gene{

    private Xref ensembl;
    private Xref ensemblGenome;
    private Xref entrezGeneId;
    private Xref refseq;

    protected IntactGene() {
        super();
    }

    public IntactGene(String name) {
        super(name, CvTermUtils.createGeneInteractorType());
    }

    public IntactGene(String name, String fullName) {
        super(name, fullName, CvTermUtils.createGeneInteractorType());
    }

    public IntactGene(String name, Organism organism) {
        super(name, CvTermUtils.createGeneInteractorType(), organism);
    }

    public IntactGene(String name, String fullName, Organism organism) {
        super(name, fullName, CvTermUtils.createGeneInteractorType(), organism);
    }

    public IntactGene(String name, Xref uniqueId) {
        super(name, CvTermUtils.createGeneInteractorType(), uniqueId);
    }

    public IntactGene(String name, String fullName, Xref uniqueId) {
        super(name, fullName, CvTermUtils.createGeneInteractorType(), uniqueId);
    }

    public IntactGene(String name, Organism organism, Xref uniqueId) {
        super(name, CvTermUtils.createGeneInteractorType(), organism, uniqueId);
    }

    public IntactGene(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, CvTermUtils.createGeneInteractorType(), organism, uniqueId);
    }

    public IntactGene(String name, String fullName, String ensembl) {
        super(name, fullName, CvTermUtils.createGeneInteractorType());

        if (ensembl != null){
            setEnsembl(ensembl);
        }
    }

    public IntactGene(String name, CvTerm type, Xref ensembl) {
        super(name, type != null ? type : CvTermUtils.createGeneInteractorType());
        this.ensembl = ensembl;
    }

    public IntactGene(String name, String fullName, CvTerm type, Xref ensembl) {
        super(name, fullName, type != null ? type : CvTermUtils.createGeneInteractorType());
        this.ensembl = ensembl;
    }

    public IntactGene(String name, CvTerm type, Organism organism, Xref ensembl) {
        super(name, type != null ? type : CvTermUtils.createGeneInteractorType(), organism);
        this.ensembl = ensembl;
    }

    public IntactGene(String name, String fullName, CvTerm type, Organism organism, Xref ensembl) {
        super(name, fullName, type != null ? type : CvTermUtils.createGeneInteractorType(), organism);
        this.ensembl = ensembl;
    }

    public IntactGene(String name, CvTerm type, Xref uniqueId, Xref ensembl) {
        super(name, type != null ? type : CvTermUtils.createGeneInteractorType(), uniqueId);
        this.ensembl = ensembl;
    }

    public IntactGene(String name, String fullName, CvTerm type, Xref uniqueId, Xref ensembl) {
        super(name, fullName, type != null ? type : CvTermUtils.createGeneInteractorType(), uniqueId);
        this.ensembl = ensembl;
    }

    public IntactGene(String name, CvTerm type, Organism organism, Xref uniqueId, Xref ensembl) {
        super(name, type != null ? type : CvTermUtils.createGeneInteractorType(), organism, uniqueId);
        this.ensembl = ensembl;
    }

    public IntactGene(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId, Xref ensembl) {
        super(name, fullName, type != null ? type : CvTermUtils.createGeneInteractorType(), organism, uniqueId);
        this.ensembl = ensembl;
    }

    public IntactGene(String name, Organism organism, String ensembl) {
        super(name, CvTermUtils.createGeneInteractorType(), organism);
        if (ensembl != null){
            setEnsembl(ensembl);
        }
    }

    public IntactGene(String name, String fullName, Organism organism, String ensembl) {
        super(name, fullName, CvTermUtils.createGeneInteractorType(), organism);
        if (ensembl != null){
            setEnsembl(ensembl);
        }
    }

    public IntactGene(String name, CvTerm type) {
        super(name, type != null ? type : CvTermUtils.createGeneInteractorType());
    }

    public IntactGene(String name, String fullName, CvTerm type) {
        super(name, fullName, type != null ? type : CvTermUtils.createGeneInteractorType());
    }

    public IntactGene(String name, CvTerm type, Organism organism) {
        super(name, type != null ? type : CvTermUtils.createGeneInteractorType(), organism);
    }

    public IntactGene(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type != null ? type : CvTermUtils.createGeneInteractorType(), organism);
    }

    @Override
    /**
     * Return the first ensembl identifier if provided, otherwise the first ensemblGenomes if provided, otherwise
     * the first entrez/gene id if provided, otherwise the first refseq id if provided
     * otherwise the first identifier in the list of identifiers
     */
    @Transient
    public Xref getPreferredIdentifier() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return ensembl != null ? ensembl : (ensemblGenome != null ? ensemblGenome : (entrezGeneId != null ? entrezGeneId : (refseq != null ? refseq : super.getPreferredIdentifier())));
    }

    @Transient
    public String getEnsembl() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.ensembl != null ? this.ensembl.getId() : null;
    }
    // TODO fetch proper cv term
    public void setEnsembl(String ac) {
        Collection<Xref> geneIdentifiers = getIdentifiers();

        // add new ensembl if not null
        if (ac != null){
            CvTerm ensemblDatabase = IntactUtils.createMIDatabase(Xref.ENSEMBL, Xref.ENSEMBL_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old ensembl if not null
            if (this.ensembl != null){
                geneIdentifiers.remove(this.ensembl);
            }
            this.ensembl = new InteractorXref(ensemblDatabase, ac, identityQualifier);
            geneIdentifiers.add(this.ensembl);
        }
        // remove all ensembl if the collection is not empty
        else if (!geneIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(geneIdentifiers, Xref.ENSEMBL_MI, Xref.ENSEMBL);
            this.ensembl = null;
        }
    }

    @Transient
    public String getEnsemblGenome() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.ensemblGenome != null ? this.ensemblGenome.getId() : null;
    }
    // TODO fetch proper cv term
    public void setEnsemblGenome(String ac) {
        Collection<Xref> geneIdentifiers = getIdentifiers();

        // add new ensembl genomes if not null
        if (ac != null){
            CvTerm ensemblGenomesDatabase = IntactUtils.createMIDatabase(Xref.ENSEMBL_GENOMES, Xref.ENSEMBL_GENOMES_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old ensembl genome if not null
            if (this.ensemblGenome != null){
                geneIdentifiers.remove(this.ensemblGenome);
            }
            this.ensemblGenome = new InteractorXref(ensemblGenomesDatabase, ac, identityQualifier);
            geneIdentifiers.add(this.ensemblGenome);
        }
        // remove all ensembl genomes if the collection is not empty
        else if (!geneIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(geneIdentifiers, Xref.ENSEMBL_GENOMES_MI, Xref.ENSEMBL_GENOMES);
            this.ensemblGenome = null;
        }
    }

    @Transient
    public String getEntrezGeneId() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.entrezGeneId != null ? this.entrezGeneId.getId() : null;
    }
    // TODO fetch proper cv term
    public void setEntrezGeneId(String id) {
        Collection<Xref> geneIdentifiers = getIdentifiers();

        // add new entrez gene id genomes if not null
        if (id != null){
            CvTerm entrezDatabase = IntactUtils.createMIDatabase(Xref.ENTREZ_GENE, Xref.ENTREZ_GENE_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old entrez gene id if not null
            if (this.entrezGeneId!= null){
                geneIdentifiers.remove(this.entrezGeneId);
            }
            this.entrezGeneId = new InteractorXref(entrezDatabase, id, identityQualifier);
            geneIdentifiers.add(this.entrezGeneId);
        }
        // remove all ensembl genomes if the collection is not empty
        else if (!geneIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(geneIdentifiers, Xref.ENTREZ_GENE_MI, Xref.ENTREZ_GENE);
            this.entrezGeneId = null;
        }
    }

    @Transient
    public String getRefseq() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.refseq != null ? this.refseq.getId() : null;
    }
    // TODO fetch proper cv term
    public void setRefseq(String ac) {
        Collection<Xref> geneIdentifiers = getIdentifiers();

        // add new refseq if not null
        if (ac != null){
            CvTerm refseqDatabase = IntactUtils.createMIDatabase(Xref.REFSEQ, Xref.REFSEQ_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove refseq if not null
            if (this.refseq!= null){
                geneIdentifiers.remove(this.refseq);
            }
            this.refseq = new InteractorXref(refseqDatabase, ac, identityQualifier);
            geneIdentifiers.add(this.refseq);
        }
        // remove all ensembl genomes if the collection is not empty
        else if (!geneIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(geneIdentifiers, Xref.REFSEQ_MI, Xref.REFSEQ);
            this.refseq = null;
        }
    }

    @Override
    public String toString() {
        return ensembl != null ? ensembl.getId() : (ensemblGenome != null ? ensemblGenome.getId() : (entrezGeneId != null ? entrezGeneId.getId() : (refseq != null ? refseq.getId() : super.toString())));
    }

    protected void processAddedIdentifierEvent(Xref added) {
        // the added identifier is ensembl and it is not the current ensembl identifier
        if (ensembl != added && XrefUtils.isXrefFromDatabase(added, Xref.ENSEMBL_MI, Xref.ENSEMBL)){
            // the current ensembl identifier is not identity, we may want to set ensembl Identifier
            if (!XrefUtils.doesXrefHaveQualifier(ensembl, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the ensembl identifier is not set, we can set the ensembl identifier
                if (ensembl == null){
                    ensembl = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    ensembl = added;
                }
                // the added xref is secondary object and the current ensembl identifier is not a secondary object, we reset ensembl identifier
                else if (!XrefUtils.doesXrefHaveQualifier(ensembl, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    ensembl = added;
                }
            }
        }
        // the added identifier is ensembl genomes and it is not the current ensembl genomes identifier
        else if (ensemblGenome != added && XrefUtils.isXrefFromDatabase(added, Xref.ENSEMBL_GENOMES_MI, Xref.ENSEMBL_GENOMES)){
            // the current ensembl genomes identifier is not identity, we may want to set ensembl genomes Identifier
            if (!XrefUtils.doesXrefHaveQualifier(ensemblGenome, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the ensembl genomes Identifier is not set, we can set the ensembl genomes Identifier
                if (ensemblGenome == null){
                    ensemblGenome = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    ensemblGenome = added;
                }
                // the added xref is secondary object and the current ensembl genomes Identifier is not a secondary object, we reset ensembl genomes Identifier
                else if (!XrefUtils.doesXrefHaveQualifier(ensemblGenome, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    ensemblGenome = added;
                }
            }
        }
        // the added identifier is entrez gene id and it is not the current entrez gene id
        else if (entrezGeneId != added && XrefUtils.isXrefFromDatabase(added, Xref.ENTREZ_GENE_MI, Xref.ENTREZ_GENE)){
            // the current entrez gene id is not identity, we may want to set entrez gene id
            if (!XrefUtils.doesXrefHaveQualifier(entrezGeneId, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the entrez gene id is not set, we can set the entrez gene idr
                if (entrezGeneId == null){
                    entrezGeneId = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    entrezGeneId = added;
                }
                // the added xref is secondary object and the current entrez gene id is not a secondary object, we reset entrez gene id
                else if (!XrefUtils.doesXrefHaveQualifier(entrezGeneId, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    entrezGeneId = added;
                }
            }
        }
        // the added identifier is refseq id and it is not the current refseq id
        else if (refseq != added && XrefUtils.isXrefFromDatabase(added, Xref.REFSEQ_MI, Xref.REFSEQ)){
            // the current refseq id is not identity, we may want to set refseq id
            if (!XrefUtils.doesXrefHaveQualifier(refseq, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the refseq id is not set, we can set the refseq id
                if (refseq == null){
                    refseq = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    refseq = added;
                }
                // the added xref is secondary object and the current refseq id is not a secondary object, we reset refseq id
                else if (!XrefUtils.doesXrefHaveQualifier(entrezGeneId, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    refseq = added;
                }
            }
        }
    }

    protected void processRemovedIdentifierEvent(Xref removed) {
        if (ensembl != null && ensembl.equals(removed)){
            ensembl = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.ENSEMBL_MI, Xref.ENSEMBL);
        }
        else if (ensemblGenome != null && ensemblGenome.equals(removed)){
            ensemblGenome = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.ENSEMBL_GENOMES_MI, Xref.ENSEMBL_GENOMES);
        }
        else if (entrezGeneId != null && entrezGeneId.equals(removed)){
            entrezGeneId = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.ENTREZ_GENE_MI, Xref.ENTREZ_GENE);
        }
        else if (refseq != null &&refseq.equals(removed)){
            refseq = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.REFSEQ_MI, Xref.REFSEQ);
        }
    }

    protected void clearPropertiesLinkedToIdentifiers() {
        ensembl = null;
        ensemblGenome = null;
        entrezGeneId = null;
        refseq = null;
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(Gene.GENE, Gene.GENE_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }
}
