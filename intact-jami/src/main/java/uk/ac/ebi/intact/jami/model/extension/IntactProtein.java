package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AliasUtils;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Intact implementation of checksum
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@javax.persistence.Entity
@DiscriminatorValue( "protein" )
public class IntactProtein extends IntactPolymer implements Protein{

    private Xref uniprotkb;
    private Xref refseq;
    private Alias geneName;
    private Checksum rogid;

    protected IntactProtein(){
        super();
    }

    public IntactProtein(String name, CvTerm type) {
        super(name, type);
    }

    public IntactProtein(String name, String fullName, CvTerm type) {
        super(name, fullName, type);
    }

    public IntactProtein(String name, CvTerm type, Organism organism) {
        super(name, type, organism);
    }

    public IntactProtein(String name, String fullName, CvTerm type, Organism organism) {
        super(name, fullName, type, organism);
    }

    public IntactProtein(String name, CvTerm type, Xref uniqueId) {
        super(name, type, uniqueId);
    }

    public IntactProtein(String name, String fullName, CvTerm type, Xref uniqueId) {
        super(name, fullName, type, uniqueId);
    }

    public IntactProtein(String name, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, type, organism, uniqueId);
    }

    public IntactProtein(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId) {
        super(name, fullName, type, organism, uniqueId);
    }

    public IntactProtein(String name) {
        super(name);
    }

    public IntactProtein(String name, String fullName) {
        super(name, fullName);
    }

    public IntactProtein(String name, Organism organism) {
        super(name, organism);
    }

    public IntactProtein(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactProtein(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactProtein(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactProtein(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactProtein(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);
    }

    /**
     * The first uniprokb if provided, then the first refseq identifier if provided, otherwise the first identifier in the list
     * @return
     */
    @Override
    @Transient
    public Xref getPreferredIdentifier() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return uniprotkb != null ? uniprotkb : (refseq != null ? refseq : super.getPreferredIdentifier());
    }

    @Transient
    public String getUniprotkb() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.uniprotkb != null ? this.uniprotkb.getId() : null;
    }

    public void setUniprotkb(String ac) {
        Collection<Xref> proteinIdentifiers = getIdentifiers();

        // add new uniprotkb if not null
        if (ac != null){
            CvTerm uniprotkbDatabase = IntactUtils.createMIDatabase(Xref.UNIPROTKB, Xref.UNIPROTKB_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old uniprotkb if not null
            if (this.uniprotkb != null){
                proteinIdentifiers.remove(this.uniprotkb);
            }
            this.uniprotkb = new InteractorXref(uniprotkbDatabase, ac, identityQualifier);
            proteinIdentifiers.add(this.uniprotkb);
        }
        // remove all uniprotkb if the collection is not empty
        else if (!proteinIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(proteinIdentifiers, Xref.UNIPROTKB_MI, Xref.UNIPROTKB);
            this.uniprotkb = null;
        }
    }

    @Transient
    public String getRefseq() {
        // initialise identifiers if not done yet
        getIdentifiers();
        return this.refseq != null ? this.refseq.getId() : null;
    }

    public void setRefseq(String ac) {
        Collection<Xref> proteinIdentifiers = getIdentifiers();

        // add new refseq if not null
        if (ac != null){
            CvTerm refseqDatabase = IntactUtils.createMIDatabase(Xref.REFSEQ, Xref.REFSEQ_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old refseq if not null
            if (this.refseq != null){
                proteinIdentifiers.remove(this.refseq);
            }
            this.refseq = new InteractorXref(refseqDatabase, ac, identityQualifier);
            proteinIdentifiers.add(this.refseq);
        }
        // remove all refseq if the collection is not empty
        else if (!proteinIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(proteinIdentifiers, Xref.REFSEQ_MI, Xref.REFSEQ);
            this.refseq = null;
        }
    }

    @Transient
    public String getGeneName() {
        // initialise aliases if not done yet
        getAliases();
        return this.geneName != null ? this.geneName.getName() : null;
    }

    public void setGeneName(String name) {
        Collection<Alias> proteinAliases = getAliases();

        // add new gene name if not null
        if (name != null){
            CvTerm geneNameType = IntactUtils.createMIAliasType(Alias.GENE_NAME, Alias.GENE_NAME_MI);
            // first remove old gene name if not null
            if (this.geneName != null){
                proteinAliases.remove(this.geneName);
            }
            this.geneName = new InteractorAlias(geneNameType, name);
            proteinAliases.add(this.geneName);
        }
        // remove all gene names if the collection is not empty
        else if (!proteinAliases.isEmpty()) {
            AliasUtils.removeAllAliasesWithType(proteinAliases, Alias.GENE_NAME_MI, Alias.GENE_NAME);
            this.geneName = null;
        }
    }

    @Transient
    public String getRogid() {
        // initialise checksum if not done yet
        getChecksums();
        return this.rogid != null ? this.rogid.getValue() : null;
    }

    public void setRogid(String rogid) {
        Collection<Checksum> proteinChecksums = getChecksums();

        if (rogid != null){
            CvTerm rogidMethod = IntactUtils.createMITopic(Checksum.ROGID, null);
            // first remove old rogid
            if (this.rogid != null){
                proteinChecksums.remove(this.rogid);
            }
            this.rogid = new InteractorChecksum(rogidMethod, rogid);
            proteinChecksums.add(this.rogid);
        }
        // remove all smiles if the collection is not empty
        else if (!proteinChecksums.isEmpty()) {
            ChecksumUtils.removeAllChecksumWithMethod(proteinChecksums, Checksum.ROGID_MI, Checksum.ROGID);
            this.rogid = null;
        }
    }

    @Override
    @Column(name = "objclass", nullable = false, insertable = false, updatable = false)
    @NotNull
    protected String getObjClass() {
        return "uk.ac.ebi.intact.model.ProteinImpl";
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(Protein.PROTEIN, Protein.PROTEIN_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }

    @Override
    protected void initialiseChecksums() {
        super.setPersistentChecksums(new ProteinChecksumList(null));
    }

    @Override
    protected void initialiseAliases() {
        super.setPersistentAliases(new ProteinAliasList(null));
    }

    @Override
    protected void setPersistentAliases(Collection<Alias> aliases) {
        super.setPersistentAliases(new ProteinAliasList(aliases));
        for (Alias alias : super.getAliases()){
            processAddedAliasEvent(alias);
        }
    }

    @Override
    protected void setPersistentChecksums(Collection<Checksum> checksums) {
        super.setPersistentChecksums(new ProteinChecksumList(checksums));
        for (Checksum check : super.getChecksums()){
            processAddedChecksumEvent(check);
        }
    }

    protected void processAddedAliasEvent(Alias added) {
        // the added alias is gene name and it is not the current gene name
        if (geneName == null && AliasUtils.doesAliasHaveType(added, Alias.GENE_NAME_MI, Alias.GENE_NAME)){
            geneName = added;
        }
    }

    protected void processRemovedAliasEvent(Alias removed) {
        if (geneName != null && geneName.equals(removed)){
            geneName = AliasUtils.collectFirstAliasWithType(getAliases(), Alias.GENE_NAME_MI, Alias.GENE_NAME);
        }
    }

    protected void clearPropertiesLinkedToAliases() {
        geneName = null;
    }

    protected void processAddedChecksumEvent(Checksum added) {
        if (rogid == null && ChecksumUtils.doesChecksumHaveMethod(added, Checksum.ROGID_MI, Checksum.ROGID)){
            // the rogid is not set, we can set the rogid
            rogid = added;
        }
    }

    protected void processRemovedChecksumEvent(Checksum removed) {
        if (rogid != null && rogid.equals(removed)){
            rogid = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), Checksum.ROGID_MI, Checksum.ROGID);
        }
    }

    protected void clearPropertiesLinkedToChecksums() {
        rogid = null;
    }

    @Override
    protected void processAddedIdentifierEvent(Xref added) {
        // the added identifier is uniprotkb and it is not the current uniprotkb identifier
        if (uniprotkb != added && XrefUtils.isXrefFromDatabase(added, Xref.UNIPROTKB_MI, Xref.UNIPROTKB)){
            // the current uniprotkb identifier is not identity, we may want to set uniprotkb Identifier
            if (!XrefUtils.doesXrefHaveQualifier(uniprotkb, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the uniprotkb identifier is not set, we can set the uniprotkb identifier
                if (uniprotkb == null){
                    uniprotkb = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    uniprotkb = added;
                }
                // the added xref is secondary object and the current uniprotkb identifier is not a secondary object, we reset uniprotkb identifier
                else if (!XrefUtils.doesXrefHaveQualifier(uniprotkb, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    uniprotkb = added;
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
                else if (!XrefUtils.doesXrefHaveQualifier(refseq, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    refseq = added;
                }
            }
        }
    }

    @Override
    protected void processRemovedIdentifierEvent(Xref removed) {
        if (uniprotkb != null && uniprotkb.equals(removed)){
            uniprotkb = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.UNIPROTKB_MI, Xref.UNIPROTKB);
        }
        else if (refseq != null && refseq.equals(removed)){
            refseq = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.REFSEQ_MI, Xref.REFSEQ);
        }
    }

    @Override
    protected void clearPropertiesLinkedToIdentifiers() {
        uniprotkb = null;
        refseq = null;
    }

    @Override
    public String toString() {
        return geneName != null ? geneName.getName() : (uniprotkb != null ? uniprotkb.getId() : (refseq != null ? refseq.getId() : super.toString()));
    }

    protected class ProteinChecksumList extends PersistentChecksumList {
        public ProteinChecksumList(Collection<Checksum> checksums){
            super(checksums);
        }

        @Override
        public boolean add(Checksum xref) {
            if(super.add(xref)){
                processAddedChecksumEvent(xref);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (super.remove(o)){
                processRemovedChecksumEvent((Checksum) o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean hasChanged = false;
            for (Object annot : c){
                if (remove(annot)){
                    hasChanged = true;
                }
            }
            return hasChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            List<Checksum> existingObject = new ArrayList<Checksum>(this);

            boolean removed = false;
            for (Checksum o : existingObject){
                if (!c.contains(o)){
                    if (remove(o)){
                        removed = true;
                    }
                }
            }

            return removed;
        }

        @Override
        public void clear() {
            super.clear();
            clearPropertiesLinkedToChecksums();
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Checksum added) {
            return false;
        }

        @Override
        protected Checksum processOrWrapElementToAdd(Checksum added) {
            return added;
        }
    }

    protected class ProteinAliasList extends PersistentAliasList {
        public ProteinAliasList(Collection<Alias> aliases){
            super(aliases);
        }

        @Override
        public boolean add(Alias xref) {
            if(super.add(xref)){
                processAddedAliasEvent(xref);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (super.remove(o)){
                processRemovedAliasEvent((Alias) o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean hasChanged = false;
            for (Object annot : c){
                if (remove(annot)){
                    hasChanged = true;
                }
            }
            return hasChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            List<Alias> existingObject = new ArrayList<Alias>(this);

            boolean removed = false;
            for (Alias o : existingObject){
                if (!c.contains(o)){
                    if (remove(o)){
                        removed = true;
                    }
                }
            }

            return removed;
        }

        @Override
        public void clear() {
            super.clear();
            clearPropertiesLinkedToAliases();
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Alias added) {
            return false;
        }

        @Override
        protected Alias processOrWrapElementToAdd(Alias added) {
            return added;
        }
    }
}
