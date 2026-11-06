package com.example.data

import com.example.data.model.DocumentChunk
import com.example.data.model.EvidenceLevel
import com.example.data.model.MedicinalPlant
import com.example.data.model.SourceReference

object CuratedKnowledgeBase {

    val curatedReferences: List<SourceReference> = listOf(
        SourceReference(
            id = "REF-AFRA-01",
            title = "Artemisia afra: A review of its botany, traditional uses, phytochemistry and pharmacology",
            authors = "Liu, N. Q., Van der Kooy, F., & Verpoorte, R.",
            year = 2009,
            journal = "South African Journal of Botany, 75(2), 185-195",
            doiOrUrl = "https://doi.org/10.1016/j.sajb.2008.11.009",
            evidenceType = "Comprehensive Systematic Review"
        ),
        SourceReference(
            id = "REF-AFRA-02",
            title = "A review of commercially important African medicinal plants",
            authors = "Van Wyk, B. E.",
            year = 2008,
            journal = "Journal of Ethnopharmacology, 119(3), 342-355",
            doiOrUrl = "https://doi.org/10.1016/j.jep.2008.06.035",
            evidenceType = "Ethnobotanical & Pharmacognosy Survey"
        ),
        SourceReference(
            id = "REF-MORI-01",
            title = "Cultivation, Genetic, Ethnopharmacology, Phytochemistry and Pharmacology of Moringa oleifera Leaves: An Overview",
            authors = "Leone, A., Spada, A., Battezzati, A., Schiraldi, A., Aristil, J., & Bertoli, S.",
            year = 2015,
            journal = "International Journal of Molecular Sciences, 16(6), 12791-12835",
            doiOrUrl = "https://doi.org/10.3390/ijms160612791",
            evidenceType = "Pharmacological & Phytochemical Overview"
        ),
        SourceReference(
            id = "REF-MORI-02",
            title = "Review of the Safety and Efficacy of Moringa oleifera",
            authors = "Stohs, S. J., & Hartman, M. J.",
            year = 2015,
            journal = "Phytotherapy Research, 29(6), 796-804",
            doiOrUrl = "https://doi.org/10.1002/ptr.5325",
            evidenceType = "Toxicology & Clinical Review"
        ),
        SourceReference(
            id = "REF-VERN-01",
            title = "Vernonia amygdalina, an ethnoveterinary and ethnomedical used green vegetable with multiple bioactivities",
            authors = "Yeap, S. K., Ho, W. Y., Liang, W. S., Beh, C. Y., Koh, S. P., & Alitheen, N. B.",
            year = 2010,
            journal = "Journal of Medicinal Plants Research, 4(25), 2787-2812",
            doiOrUrl = "https://doi.org/10.5897/JMPR.9000007",
            evidenceType = "Biological & Pharmacological Review"
        ),
        SourceReference(
            id = "REF-VERN-02",
            title = "Antioxidative and chemopreventive properties of Vernonia amygdalina and Garcinia kola",
            authors = "Farombi, E. O., & Owoeye, O.",
            year = 2011,
            journal = "African Journal of Medicine and Medical Sciences, 40(Suppl), 5-17",
            doiOrUrl = "PMID: 22435219",
            evidenceType = "Laboratory In Vivo / In Vitro Study"
        ),
        SourceReference(
            id = "REF-HARP-01",
            title = "Herbal medicine for low back pain: A Cochrane review",
            authors = "Gagnier, J. J., van Tulder, M. W., Berman, B., & Bombardieri, D.",
            year = 2007,
            journal = "Spine, 32(1), 82-92",
            doiOrUrl = "https://doi.org/10.1097/01.brs.0000252097.42823.50",
            evidenceType = "Cochrane Systematic Review & Meta-Analysis"
        ),
        SourceReference(
            id = "REF-HARP-02",
            title = "Devil's Claw - A review of the ethnobotany, phytochemistry and biological activity of Harpagophytum procumbens",
            authors = "Mncwangi, N., Chen, W., Vermaak, I., Viljoen, A. M., & Gericke, N.",
            year = 2012,
            journal = "Journal of Ethnopharmacology, 143(3), 755-778",
            doiOrUrl = "https://doi.org/10.1016/j.jep.2012.08.013",
            evidenceType = "Ethnobotanical & Pharmacological Review"
        ),
        SourceReference(
            id = "REF-CATH-01",
            title = "Catharanthus roseus: Phytochemical and pharmacological properties",
            authors = "Nejat, N., Valdiani, A., Cahill, D., Tan, Y. H., Maziah, M., & Abiri, R.",
            year = 2015,
            journal = "European Journal of Medicinal Plants, 5(2), 118-128",
            doiOrUrl = "https://doi.org/10.9734/EJMP/2015/13358",
            evidenceType = "Phytochemical & Pharmacological Review"
        ),
        SourceReference(
            id = "REF-CATH-02",
            title = "The discovery of the vinca alkaloids—chemotherapeutic agents against cancer",
            authors = "Noble, R. L.",
            year = 1990,
            journal = "Biochemistry and Cell Biology, 68(12), 1344-1351",
            doiOrUrl = "https://doi.org/10.1139/o90-197",
            evidenceType = "Historical Pharmacology & Clinical Discovery"
        )
    )

    val curatedPlants: List<MedicinalPlant> = listOf(
        MedicinalPlant(
            id = "PLANT-001",
            scientificName = "Artemisia afra Jacq. ex Willd.",
            commonNamesList = "African Wormwood, Umhlonyane, Lengana, Wilde-als, Zuring",
            family = "Asteraceae",
            localNamesJson = "{\"Zulu\": \"Umhlonyane\", \"Xhosa\": \"Umhlonyane\", \"Sotho\": \"Lengana\", \"Tswana\": \"Lengana\", \"Afrikaans\": \"Wilde-als\", \"Amharic\": \"Chugugit\"}",
            plantPartsList = "Leaves, Flowering tops, Young stems",
            geographicalContext = "Widely distributed across Southern, Eastern, and Central Africa (South Africa, Lesotho, Zimbabwe, Ethiopia, Kenya). Found primarily in montane grasslands, forest margins, and rocky slopes.",
            traditionalUsesList = "Steam inhalation for bronchial congestion, cough, and colds | Infusion of leaves for fever, digestive discomfort, and dyspepsia | Heated leaf poultices applied topically for neuralgia and muscular ache | Traditional bitter tonic for stimulating appetite and liver function",
            preparationMethod = "Traditional hot water infusion (steeping 1-2 teaspoons of dried leaves in boiling water for 10-15 minutes). For respiratory relief, fresh foliage is boiled in water and the resulting fragrant steam is inhaled under a towel.",
            phytochemicalsList = "Alpha-thujone, Beta-thujone, 1,8-Cineole (Eucalyptol), Camphor, Chrysoeriol, Luteolin, Scopoletin, Isoquercitrin",
            laboratoryEvidence = "Demonstrated in vitro antimicrobial activity against respiratory pathogens (Streptococcus pneumoniae, Klebsiella pneumoniae). Moderate in vitro antiplasmodial activity against Plasmodium falciparum, although markedly less potent than Artemisia annua due to the virtual absence of artemisinin. In vitro anti-inflammatory assays indicate inhibition of NF-kB and COX-2 expression in LPS-stimulated macrophages.",
            clinicalEvidence = "Human clinical trials remain extremely limited. Despite extensive historical ethnobotanical documentation across Southern Africa, robust, double-blind randomized clinical trial evidence demonstrating efficacy in human diseases is currently lacking. Symptomatic reports are based predominantly on observational and traditional use records.",
            safetyInformation = "WARNING: Contains thujone, a documented monoterpene neurotoxin capable of inducing epileptiform convulsions and CNS hyper-excitability with high doses or prolonged consumption. STRICTLY CONTRAINDICATED IN PREGNANCY due to proven uterine stimulant and abortifacient actions in ethnopharmacological reports. Not recommended during breastfeeding, in young children, or in patients with pre-existing seizure disorders.",
            limitations = "Substantial phytochemical variability between distinct regional chemotypes (some chemotypes are dominated by thujone while others are 1,8-cineole rich). Clinical pharmacokinetic and toxicological safety margins in humans have not been adequately established by modern clinical drug trials.",
            referencesJson = "[{\"id\":\"REF-AFRA-01\",\"title\":\"Artemisia afra: A review of its botany, traditional uses, phytochemistry and pharmacology\",\"authors\":\"Liu, N. Q. et al.\",\"year\":2009,\"journal\":\"South African Journal of Botany, 75(2), 185-195\",\"doiOrUrl\":\"https://doi.org/10.1016/j.sajb.2008.11.009\",\"evidenceType\":\"Review\"},{\"id\":\"REF-AFRA-02\",\"title\":\"A review of commercially important African medicinal plants\",\"authors\":\"Van Wyk, B. E.\",\"year\":2008,\"journal\":\"Journal of Ethnopharmacology, 119(3), 342-355\",\"doiOrUrl\":\"https://doi.org/10.1016/j.jep.2008.06.035\",\"evidenceType\":\"Survey\"}]",
            reviewStatus = EvidenceLevel.LABORATORY_EVIDENCE,
            isUserCreated = false,
            imageDrawableName = "botanical_hero"
        ),
        MedicinalPlant(
            id = "PLANT-002",
            scientificName = "Moringa oleifera Lam.",
            commonNamesList = "Moringa, Miracle Tree, Drumstick Tree, Horseradish Tree, Zogale, Ewe Igbale",
            family = "Moringaceae",
            localNamesJson = "{\"Hausa\": \"Zogale\", \"Yoruba\": \"Ewe Igbale / Idagbo monoye\", \"Igbo\": \"Okwe oyibo / Odudu oyibo\", \"Swahili\": \"Mzunge / Mlonge\", \"Amharic\": \"Shiferaw\"}",
            plantPartsList = "Leaves, Pods, Seeds, Root bark (toxic)",
            geographicalContext = "Cultivated extensively across sub-Saharan Africa, particularly in the Sahel belt (Nigeria, Niger, Mali, Senegal), East Africa (Kenya, Tanzania), and tropical savannah regions as a nutritious dietary staple and medicinal boundary crop.",
            traditionalUsesList = "Culinary leafy vegetable consumed daily in nutritional broths | Traditional management of high blood pressure and hyperglycemia | Ground seed paste historically applied for water clarification and antibacterial purification | Leaf poultice applied to swellings and superficial skin cuts",
            preparationMethod = "Fresh mature leaves cooked into soups (e.g. Hausa Miyar Zogale) or gently shade-dried and pulverized into fine green powder. Leaf powders are steeped in warm water as an herbal tea infusion (1-2 g daily).",
            phytochemicalsList = "Moringine, Glucomoringin, Quercetin, Chlorogenic acid, Kaempferol, Beta-sitosterol, Vitamin C, Isothiocyanates",
            laboratoryEvidence = "Extensive in vitro and in vivo animal assays confirm antioxidant capacity, down-regulation of pro-inflammatory cytokines (TNF-alpha, IL-6), and dose-dependent reductions in fasting blood glucose in diabetic rodent models. Seed extracts exhibit potent coagulant and bactericidal activity against waterborne pathogens due to cationic polyelectrolyte peptides.",
            clinicalEvidence = "Preliminary small-scale human clinical trials in diabetic subjects report modest reductions in postprandial blood glucose and improvements in lipid profiles (LDL reduction). However, sample sizes across published human studies remain modest (n=20-60), and methodologies vary considerably. Larger multi-center randomized controlled trials are required to establish therapeutic clinical guidelines.",
            safetyInformation = "SAFE: Moringa leaves and young immature pods have an established history of safe human dietary consumption in food amounts. CRITICAL WARNING: The root, root bark, and crude bark contain spirochin, a potentially lethal alkaloid associated with neuromuscular paralysis and cardiac depression, as well as abortifacient compounds that cause strong uterine contractions. Roots and bark MUST NOT be consumed by pregnant individuals or the general public.",
            limitations = "Nutritional content drops rapidly if leaves are exposed to direct sunlight or prolonged boiling. Commercial products frequently lack chemical standardization of active isothiocyanate and polyphenol content.",
            referencesJson = "[{\"id\":\"REF-MORI-01\",\"title\":\"Cultivation, Genetic, Ethnopharmacology, Phytochemistry and Pharmacology of Moringa oleifera Leaves: An Overview\",\"authors\":\"Leone, A. et al.\",\"year\":2015,\"journal\":\"Int. J. Mol. Sci., 16(6), 12791-12835\",\"doiOrUrl\":\"https://doi.org/10.3390/ijms160612791\",\"evidenceType\":\"Overview\"},{\"id\":\"REF-MORI-02\",\"title\":\"Review of the Safety and Efficacy of Moringa oleifera\",\"authors\":\"Stohs, S. J. & Hartman, M. J.\",\"year\":2015,\"journal\":\"Phytotherapy Research, 29(6), 796-804\",\"doiOrUrl\":\"https://doi.org/10.1002/ptr.5325\",\"evidenceType\":\"Review\"}]",
            reviewStatus = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
            isUserCreated = false,
            imageDrawableName = "botanical_hero"
        ),
        MedicinalPlant(
            id = "PLANT-003",
            scientificName = "Vernonia amygdalina Delile",
            commonNamesList = "Bitter Leaf, Ewuro, Onugbu, Oriwo, Shuwaka, Grawa",
            family = "Asteraceae",
            localNamesJson = "{\"Yoruba\": \"Ewuro\", \"Igbo\": \"Onugbu\", \"Hausa\": \"Shuwaka\", \"Edo\": \"Oriwo\", \"Amharic\": \"Grawa\", \"Luganda\": \"Mululuza\"}",
            plantPartsList = "Leaves, Roots, Twigs (chewing sticks)",
            geographicalContext = "Native to tropical sub-Saharan Africa. Abundantly cultivated and wild-harvested across West and Central Africa (Nigeria, Cameroon, Ghana, Benin) and the Great Lakes region.",
            traditionalUsesList = "Squeezed fresh leaf juice consumed as a bitter remedy for malaria and febrile illnesses | Traditional home remedy for gastrointestinal upsets, dysentery, and intestinal worms | Washed leaf cooked into traditional Nigerian soups (Ofe Onugbu) after deliberate debittering | Twigs widely chewed across rural communities for dental hygiene and antimicrobial protection",
            preparationMethod = "Traditional aqueous decoction or crushing fresh leaves by hand in clean water to extract the bitter greenish juice. In culinary preparation, leaves are thoroughly washed and macerated in repeated changes of water to leach out excess bitter sesquiterpene lactones prior to cooking.",
            phytochemicalsList = "Vernodalin, Vernolide, Vernolepin, Vernonioiside A1-B3, Luteolin-7-O-glucoside, Hydroxybenzoic acid, Saponins",
            laboratoryEvidence = "Demonstrated in vitro antiplasmodial efficacy against chloroquine-sensitive and resistant strains of Plasmodium falciparum (IC50 values between 5 and 15 ug/mL for crude organic fractions). Significant in vitro antiproliferative activity against human breast carcinoma (MCF-7) cell lines via induction of apoptosis. In vivo rodent models show marked blood glucose lowering and hepatoprotective properties against chemical toxicant challenges.",
            clinicalEvidence = "Limited human clinical trials. A clinical pilot study in Mali and Nigeria investigated Vernonia amygdalina decoctions as an adjunctive therapy for uncomplicated malaria, noting symptom clearance but delayed parasite clearance compared to standard artemisinin-based combination therapies (ACTs). It is NOT recommended as a standalone substitute for modern ACTs.",
            safetyInformation = "PREGNANCY WARNING: Ethanolic and crude aqueous leaf extracts exhibit verified oxytocic (uterotonic) contractile activity in uterine tissue preparations. In West African traditional midwifery, concentrated decoctions are sometimes used to induce labor or expel retained placenta; consequently, IT IS STRONGLY CONTRAINDICATED during early and mid-pregnancy due to potential induction of miscarriage. High doses may induce gastrointestinal nausea and emesis.",
            limitations = "Severe bitterness leads to poor compliance without debittering, but aggressive water washing leaches out substantial bioactive sesquiterpenes. Scientific extraction protocols vary widely from traditional kitchen preparations.",
            referencesJson = "[{\"id\":\"REF-VERN-01\",\"title\":\"Vernonia amygdalina, an ethnoveterinary and ethnomedical used green vegetable with multiple bioactivities\",\"authors\":\"Yeap, S. K. et al.\",\"year\":2010,\"journal\":\"J. Med. Plants Res., 4(25), 2787-2812\",\"doiOrUrl\":\"https://doi.org/10.5897/JMPR.9000007\",\"evidenceType\":\"Review\"},{\"id\":\"REF-VERN-02\",\"title\":\"Antioxidative and chemopreventive properties of Vernonia amygdalina and Garcinia kola\",\"authors\":\"Farombi, E. O. & Owoeye, O.\",\"year\":2011,\"journal\":\"Afr. J. Med. Med. Sci., 40(Suppl), 5-17\",\"doiOrUrl\":\"PMID: 22435219\",\"evidenceType\":\"Study\"}]",
            reviewStatus = EvidenceLevel.ANIMAL_RESEARCH,
            isUserCreated = false,
            imageDrawableName = "botanical_hero"
        ),
        MedicinalPlant(
            id = "PLANT-004",
            scientificName = "Harpagophytum procumbens (Burch.) DC. ex Meisn.",
            commonNamesList = "Devil's Claw, Grapple Plant, Wood Spider, Sengaparile, Duiwelsklou",
            family = "Pedaliaceae",
            localNamesJson = "{\"Setswana\": \"Sengaparile\", \"Afrikaans\": \"Duiwelsklou\", \"Nama\": \"Otjihangatene\", \"Khoisan\": \"Kx'am\"}",
            plantPartsList = "Secondary storage tubers (roots)",
            geographicalContext = "Endemic to the arid Kalahari basin of Southern Africa, predominantly distributed across Namibia, Botswana, and north-western South Africa in deep red Kalahari sands.",
            traditionalUsesList = "San and Khoi traditional decoction of dried root tubers for painful joint disorders, musculoskeletal stiffness, and severe arthritis | Digestive bitter tea for stomach discomfort and persistent low fevers | Topical root ointments applied traditionally to painful sores and lesions",
            preparationMethod = "Traditional preparation involves slicing the secondary storage tubers, drying them in desert sun, and making a slow decoction or cold water maceration. Modern pharmaceutical preparations utilize standardized dry hydroalcoholic extracts providing 50-100 mg of harpagoside daily.",
            phytochemicalsList = "Harpagoside, Harpagide, 8-para-coumaroylharpagide, Verbascoside (Acteoside), Beta-sitosterol, Stachyose",
            laboratoryEvidence = "In vitro and ex vivo pharmacological studies show potent inhibition of inflammatory cascades, specifically downregulation of cyclooxygenase-2 (COX-2), inducible nitric oxide synthase (iNOS), and matrix metalloproteinases (MMP-1, MMP-3) responsible for joint cartilage degradation.",
            clinicalEvidence = "SUBSTANTIAL CLINICAL EVIDENCE: Evaluated in multiple randomized, double-blind, placebo-controlled human trials. Cochrane Systematic Reviews conclude that standardized Harpagophytum daily doses containing 50-100 mg harpagoside provide clinically significant short-term pain relief and functional improvement in exacerbations of chronic low back pain and osteoarthritis of the hip and knee, with efficacy comparable to low-dose rofecoxib or NSAIDs in several trials.",
            safetyInformation = "CONTRAINDICATIONS: Because the bitter iridoids stimulate gastric acid secretion, Devil's Claw is strictly contraindicated in patients with active gastric or duodenal peptic ulcers. Due to potential choleretic effects, individuals with symptomatic gallstones must exercise caution or avoid use. Safety in pregnant and lactating women has not been established; traditional reports warn of potential uterotonic activity.",
            limitations = "Overharvesting of wild tubers in the Kalahari presents severe conservation and community sustainability concerns. Phytochemical potency depends heavily on distinguishing secondary tubers from nutrient-poor primary taproots.",
            referencesJson = "[{\"id\":\"REF-HARP-01\",\"title\":\"Herbal medicine for low back pain: A Cochrane review\",\"authors\":\"Gagnier, J. J. et al.\",\"year\":2007,\"journal\":\"Spine, 32(1), 82-92\",\"doiOrUrl\":\"https://doi.org/10.1097/01.brs.0000252097.42823.50\",\"evidenceType\":\"Systematic Review\"},{\"id\":\"REF-HARP-02\",\"title\":\"Devil's Claw - A review of the ethnobotany, phytochemistry and biological activity of Harpagophytum procumbens\",\"authors\":\"Mncwangi, N. et al.\",\"year\":2012,\"journal\":\"J. Ethnopharmacol., 143(3), 755-778\",\"doiOrUrl\":\"https://doi.org/10.1016/j.jep.2012.08.013\",\"evidenceType\":\"Review\"}]",
            reviewStatus = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
            isUserCreated = false,
            imageDrawableName = "botanical_hero"
        ),
        MedicinalPlant(
            id = "PLANT-005",
            scientificName = "Catharanthus roseus (L.) G.Don",
            commonNamesList = "Madagascar Periwinkle, Rose Periwinkle, Old Maid, Vinca rosea",
            family = "Apocynaceae",
            localNamesJson = "{\"Malagasy\": \"Voninkazo / Tongatse\", \"Yoruba\": \"Tapinapa\", \"Swahili\": \"Maua ya daktari\", \"Hausa\": \"Furen fatakar\"}",
            plantPartsList = "Whole aerial plant, Leaves, Roots (Caution: Highly Poisonous)",
            geographicalContext = "Native and endemic to Madagascar; now naturalized in tropical and subtropical regions across coastal and island Africa.",
            traditionalUsesList = "Traditional Malagasy and folk herbal tea for symptom management of diabetes mellitus | Folk preparation used historically as an astringent gargle and diuretic tonic",
            preparationMethod = "Traditional folk medicine historically utilized weak leaf infusions as a diabetic remedy. Modern science has completely supplanted raw ingestion with rigorous pharmaceutical extraction and purification of trace cytotoxic alkaloids.",
            phytochemicalsList = "Vinblastine, Vincristine, Ajmalicine (Raubasine), Serpentine, Catharanthine, Vindoline, Lochnerine",
            laboratoryEvidence = "Over 130 terpenoid indole alkaloids isolated. Vinblastine and vincristine bind specifically to tubulin dimers, disrupting microtubule assembly and causing mitotic arrest at metaphase in rapidly dividing cancer cells. Ajmalicine exhibits demonstrated peripheral vasodilatory and anti-adrenergic properties.",
            clinicalEvidence = "MAJOR PHARMACEUTICAL CLINICAL DISCOVERY: While folklore touted the plant for diabetes, scientific investigation by Canadian researchers (Noble, Beer, Cutts) discovered severe leukopenia in animal models, leading directly to the isolation of Vinblastine and Vincristine. These are FDA-approved, WHO Essential Medicines, forming foundational components of life-saving chemotherapy regimens for Hodgkin lymphoma, acute lymphoblastic leukemia (ALL), testicular carcinoma, and pediatric nephroblastoma.",
            safetyInformation = "CRITICAL TOXICITY WARNING: CATHARANTHUS ROSEUS IS HIGHLY TOXIC IN UNSTANDARDIZED FORM. Ingestion of raw leaves, teas, or unregulated herbal preparations presents an extreme risk of severe bone marrow suppression (leukopenia, anemia, thrombocytopenia), irreversible peripheral neuropathy, gastrointestinal paralytic ileus, and fatal toxicity. IT MUST NEVER BE CONSUMED AS A HOME HERBAL REMEDY.",
            limitations = "The concentration of chemotherapeutic alkaloids in raw plant matter is miniscule (hundreds of kilograms of leaves required to produce grams of pure drug). Folk ingestion cannot replicate targeted therapeutic chemotherapy and only exposes individuals to systemic poisoning.",
            referencesJson = "[{\"id\":\"REF-CATH-01\",\"title\":\"Catharanthus roseus: Phytochemical and pharmacological properties\",\"authors\":\"Nejat, N. et al.\",\"year\":2015,\"journal\":\"Eur. J. Med. Plants, 5(2), 118-128\",\"doiOrUrl\":\"https://doi.org/10.9734/EJMP/2015/13358\",\"evidenceType\":\"Review\"},{\"id\":\"REF-CATH-02\",\"title\":\"The discovery of the vinca alkaloids—chemotherapeutic agents against cancer\",\"authors\":\"Noble, R. L.\",\"year\":1990,\"journal\":\"Biochem. Cell Biol., 68(12), 1344-1351\",\"doiOrUrl\":\"https://doi.org/10.1139/o90-197\",\"evidenceType\":\"Historical Discovery\"}]",
            reviewStatus = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
            isUserCreated = false,
            imageDrawableName = "botanical_hero"
        )
    )

    val curatedDocumentChunks: List<DocumentChunk> = listOf(
        // Artemisia afra chunks
        DocumentChunk(
            id = "CHUNK-AFRA-01",
            plantId = "PLANT-001",
            plantScientificName = "Artemisia afra Jacq. ex Willd.",
            sourceId = "REF-AFRA-01",
            sourceTitle = "Artemisia afra: A review of its botany, traditional uses, phytochemistry and pharmacology",
            authors = "Liu, N. Q., Van der Kooy, F., & Verpoorte, R.",
            year = 2009,
            citation = "Liu et al. (2009), S. Afr. J. Bot., 75(2):185-195",
            section = "Phytochemistry",
            text = "Phytochemical profiling of Artemisia afra essential oils and extracts reveals volatile mono- and sesquiterpenoids as predominant constituents. Major volatile constituents include alpha- and beta-thujone, 1,8-cineole (eucalyptol), camphor, and alpha-pinene. Non-volatile fractions contain bioactive flavonoids including luteolin, chrysoeriol, and quercetin, along with caffeoylquinic acids and the coumarin scopoletin. Crucially, analytical HPLC and mass spectrometry investigations have consistently established that Artemisia afra contains no detectable artemisinin or only negligible trace quantities, distinguishing it biochemically from the Eurasian Artemisia annua.",
            evidenceTier = EvidenceLevel.LABORATORY_EVIDENCE,
            keywords = "artemisia afra, phytochemistry, thujone, cineole, camphor, flavonoids, luteolin, artemisinin"
        ),
        DocumentChunk(
            id = "CHUNK-AFRA-02",
            plantId = "PLANT-001",
            plantScientificName = "Artemisia afra Jacq. ex Willd.",
            sourceId = "REF-AFRA-01",
            sourceTitle = "Artemisia afra: A review of its botany, traditional uses, phytochemistry and pharmacology",
            authors = "Liu, N. Q., Van der Kooy, F., & Verpoorte, R.",
            year = 2009,
            citation = "Liu et al. (2009), S. Afr. J. Bot., 75(2):185-195",
            section = "Traditional Use & Preparation",
            text = "In traditional South African and Zulu ethnomedicine (where the plant is known as Umhlonyane) and Sotho medicine (Lengana), Artemisia afra is one of the most widely revered medicinal herbs. Leaves and leafy stems are traditionally boiled and the aromatic vapors inhaled under a blanket to treat coughs, colds, influenza, and bronchial congestion. Warm water infusions are ingested as bitter tonics to alleviate stomach cramps, feverish illnesses, and colic. Traditional healers often combine it with honey to soften its pronounced bitter taste.",
            evidenceTier = EvidenceLevel.TRADITIONAL_USE_REPORTED,
            keywords = "artemisia afra, traditional use, umhlonyane, lengana, zulu, sotho, steam inhalation, cough, colds, fever, preparation"
        ),
        DocumentChunk(
            id = "CHUNK-AFRA-03",
            plantId = "PLANT-001",
            plantScientificName = "Artemisia afra Jacq. ex Willd.",
            sourceId = "REF-AFRA-01",
            sourceTitle = "Artemisia afra: A review of its botany, traditional uses, phytochemistry and pharmacology",
            authors = "Liu, N. Q., Van der Kooy, F., & Verpoorte, R.",
            year = 2009,
            citation = "Liu et al. (2009), S. Afr. J. Bot., 75(2):185-195",
            section = "Safety & Contraindications",
            text = "SAFETY WARNING: The presence of alpha- and beta-thujone imparts potential neurotoxicity to Artemisia afra preparations when consumed in high doses or over prolonged periods. Thujone acts as a competitive GABA-A receptor antagonist, capable of lowering seizure thresholds and causing convulsions. Furthermore, ethnopharmacological records document uterine-stimulating and abortifacient actions; Artemisia afra is therefore STRICTLY CONTRAINDICATED IN PREGNANCY. It is also inadvisable for breastfeeding women, children, and individuals with epilepsy.",
            evidenceTier = EvidenceLevel.ANIMAL_RESEARCH,
            keywords = "artemisia afra, safety, toxicity, thujone, neurotoxicity, contraindication, pregnancy, miscarriage, seizures, children"
        ),
        DocumentChunk(
            id = "CHUNK-AFRA-04",
            plantId = "PLANT-001",
            plantScientificName = "Artemisia afra Jacq. ex Willd.",
            sourceId = "REF-AFRA-02",
            sourceTitle = "A review of commercially important African medicinal plants",
            authors = "Van Wyk, B. E.",
            year = 2008,
            citation = "Van Wyk (2008), J. Ethnopharmacol., 119(3):342-355",
            section = "Pharmacology & Evidence Limitations",
            text = "While in vitro studies show moderate antimicrobial activity against Gram-positive bacteria and mild in vitro inhibition of Plasmodium falciparum cultures, there is an absence of rigorous, randomized double-blind placebo-controlled human clinical trials to validate therapeutic claims. Efficacy for systemic infections remains unverified in clinical settings. The plant should not be relied upon as a substitute for certified antimalarial drugs.",
            evidenceTier = EvidenceLevel.INSUFFICIENT_OR_CONFLICTING,
            keywords = "artemisia afra, clinical evidence, limitations, malaria, antimalarial, placebo trials, unverified"
        ),

        // Moringa oleifera chunks
        DocumentChunk(
            id = "CHUNK-MORI-01",
            plantId = "PLANT-002",
            plantScientificName = "Moringa oleifera Lam.",
            sourceId = "REF-MORI-01",
            sourceTitle = "Cultivation, Genetic, Ethnopharmacology, Phytochemistry and Pharmacology of Moringa oleifera Leaves: An Overview",
            authors = "Leone, A. et al.",
            year = 2015,
            citation = "Leone et al. (2015), Int. J. Mol. Sci., 16(6):12791-12835",
            section = "Phytochemistry & Nutritional Value",
            text = "Moringa oleifera leaves represent an exceptional reservoir of protein, vitamins (A, B-complex, C), and minerals (calcium, potassium, iron). Secondary metabolites in the foliage include substantial concentrations of polyphenols (quercetin-3-O-glucoside, rutin, chlorogenic acid) and unique glucosinolates, notably 4-(alpha-L-rhamnosyloxy)-benzyl glucosinolate (glucomoringin), which enzymatically converts into the bioactive isothiocyanate moringin possessing documented anti-inflammatory and antioxidant activities.",
            evidenceTier = EvidenceLevel.LABORATORY_EVIDENCE,
            keywords = "moringa oleifera, phytochemistry, nutrition, leaves, vitamins, quercetin, chlorogenic acid, glucosinolates, isothiocyanate, moringin"
        ),
        DocumentChunk(
            id = "CHUNK-MORI-02",
            plantId = "PLANT-002",
            plantScientificName = "Moringa oleifera Lam.",
            sourceId = "REF-MORI-01",
            sourceTitle = "Cultivation, Genetic, Ethnopharmacology, Phytochemistry and Pharmacology of Moringa oleifera Leaves: An Overview",
            authors = "Leone, A. et al.",
            year = 2015,
            citation = "Leone et al. (2015), Int. J. Mol. Sci., 16(6):12791-12835",
            section = "Traditional Use & West African Context",
            text = "Across Northern Nigeria and the Sahel, known as Zogale in Hausa and Ewe Igbale in Yoruba, Moringa leaves are traditionally boiled in wholesome soups, mixed with peanut cake (Kulikuli) into nutrient-dense salads, or steeped into tea. Traditional healers prescribe it to combat maternal and infant malnutrition, lower blood sugar spikes, reduce elevated arterial tension, and support general vitality. In East Africa (Swahili: Mlonge), leaf decoctions are traditionally consumed for joint stiffness and gastric disturbances.",
            evidenceTier = EvidenceLevel.TRADITIONAL_USE_REPORTED,
            keywords = "moringa oleifera, traditional use, zogale, hausa, yoruba, ewe igbale, mlonge, nutrition, diabetes, blood pressure, preparation"
        ),
        DocumentChunk(
            id = "CHUNK-MORI-03",
            plantId = "PLANT-002",
            plantScientificName = "Moringa oleifera Lam.",
            sourceId = "REF-MORI-02",
            sourceTitle = "Review of the Safety and Efficacy of Moringa oleifera",
            authors = "Stohs, S. J., & Hartman, M. J.",
            year = 2015,
            citation = "Stohs & Hartman (2015), Phytother. Res., 29(6):796-804",
            section = "Safety, Toxicity & Plant Part Discrimination",
            text = "A critical distinction exists between plant parts: Leaf extracts show a very high safety profile in animal toxicology studies with LD50 exceeding 5000 mg/kg, and human food consumption of dried leaves (up to 5-10 g daily) has demonstrated safety without adverse events. CONTRAINDICATION & TOXICITY: Conversely, Moringa root and root bark contain the toxic alkaloid spirochin and benzyl isothiocyanate derivatives that exhibit cardiac depressive and abortifacient actions. Ingestion of root bark can trigger uterine contractions and spontaneous abortions. Root parts MUST NOT be consumed by pregnant individuals.",
            evidenceTier = EvidenceLevel.ANIMAL_RESEARCH,
            keywords = "moringa oleifera, safety, toxicity, root bark, spirochin, abortion, pregnancy, contraindication, food safety"
        ),
        DocumentChunk(
            id = "CHUNK-MORI-04",
            plantId = "PLANT-002",
            plantScientificName = "Moringa oleifera Lam.",
            sourceId = "REF-MORI-02",
            sourceTitle = "Review of the Safety and Efficacy of Moringa oleifera",
            authors = "Stohs, S. J., & Hartman, M. J.",
            year = 2015,
            citation = "Stohs & Hartman (2015), Phytother. Res., 29(6):796-804",
            section = "Clinical Trials & Glycemic Regulation",
            text = "Small randomized human crossover trials have evaluated Moringa leaf powder in type 2 diabetic subjects. Consumption of 4-8 g of leaf powder alongside high-carbohydrate meals demonstrated a modest statistically significant attenuation of postprandial blood glucose elevation (10-20% reduction) and positive shifts in fasting lipids. However, trial durations were brief (single-dose to 40 days) and study cohorts small. It should be considered an adjunct nutritional intervention rather than a replacement for prescribed insulin or oral hypoglycemics.",
            evidenceTier = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
            keywords = "moringa oleifera, clinical trials, glycemic, diabetes, blood sugar, insulin, human studies, limitations"
        ),

        // Vernonia amygdalina chunks
        DocumentChunk(
            id = "CHUNK-VERN-01",
            plantId = "PLANT-003",
            plantScientificName = "Vernonia amygdalina Delile",
            sourceId = "REF-VERN-01",
            sourceTitle = "Vernonia amygdalina, an ethnoveterinary and ethnomedical used green vegetable with multiple bioactivities",
            authors = "Yeap, S. K. et al.",
            year = 2010,
            citation = "Yeap et al. (2010), J. Med. Plants Res., 4(25):2787-2812",
            section = "Phytochemistry & Bitter Principles",
            text = "The characteristic intense bitterness of Vernonia amygdalina (Bitter Leaf) stems from a suite of sesquiterpene lactones, primarily vernodalin, vernolepin, vernolide, and hydroxyvernolide, accompanied by steroid glycosides termed vernoniosides (A1, A2, A3, B1, B2, B3). The leaves also contain significant amounts of antioxidant luteolin glycosides, flavones, and vernomygdin. Debittering processes in culinary preparation significantly wash out these water-soluble bitter sesquiterpenes into discard wash water.",
            evidenceTier = EvidenceLevel.LABORATORY_EVIDENCE,
            keywords = "vernonia amygdalina, bitter leaf, phytochemistry, sesquiterpene lactones, vernodalin, vernonioside, luteolin, debittering"
        ),
        DocumentChunk(
            id = "CHUNK-VERN-02",
            plantId = "PLANT-003",
            plantScientificName = "Vernonia amygdalina Delile",
            sourceId = "REF-VERN-01",
            sourceTitle = "Vernonia amygdalina, an ethnoveterinary and ethnomedical used green vegetable with multiple bioactivities",
            authors = "Yeap, S. K. et al.",
            year = 2010,
            citation = "Yeap et al. (2010), J. Med. Plants Res., 4(25):2787-2812",
            section = "Traditional Medicine & West African Heritage",
            text = "Known as Ewuro in Yoruba, Onugbu in Igbo, and Shuwaka in Hausa, Vernonia amygdalina is woven deeply into traditional healing systems. The raw juice squeezed from crushed leaves is taken orally to purge intestinal parasites, ease fever, and regulate digestive colic. Bitter leaf chewing sticks are utilized daily for oral hygiene and gingivitis prevention. In traditional postpartum care in Nigeria, warm decoctions are administered to contract the uterus and cleanse lochial discharge after child delivery.",
            evidenceTier = EvidenceLevel.TRADITIONAL_USE_REPORTED,
            keywords = "vernonia amygdalina, ewuro, onugbu, shuwaka, traditional use, fever, malaria, parasites, chewing stick, postpartum"
        ),
        DocumentChunk(
            id = "CHUNK-VERN-03",
            plantId = "PLANT-003",
            plantScientificName = "Vernonia amygdalina Delile",
            sourceId = "REF-VERN-02",
            sourceTitle = "Antioxidative and chemopreventive properties of Vernonia amygdalina and Garcinia kola",
            authors = "Farombi, E. O., & Owoeye, O.",
            year = 2011,
            citation = "Farombi & Owoeye (2011), Afr. J. Med. Med. Sci., 40:5-17",
            section = "Laboratory Findings & Antimalarial Screening",
            text = "Laboratory screenings of crude organic and aqueous Vernonia leaf fractions have demonstrated clear in vitro antiparasitic action against chloroquine-sensitive and resistant strains of Plasmodium falciparum, with vernodalin and vernolide accounting for the bioactivity. In addition, aqueous extracts demonstrate dose-dependent anti-hyperglycemic and free-radical scavenging action in streptozotocin-induced diabetic rats. Antiproliferative screening has shown inhibition of breast cancer cell growth through apoptotic pathway activation.",
            evidenceTier = EvidenceLevel.ANIMAL_RESEARCH,
            keywords = "vernonia amygdalina, laboratory evidence, in vitro, malaria, plasmodium, diabetes, cancer, antiproliferative, antioxidant"
        ),
        DocumentChunk(
            id = "CHUNK-VERN-04",
            plantId = "PLANT-003",
            plantScientificName = "Vernonia amygdalina Delile",
            sourceId = "REF-VERN-01",
            sourceTitle = "Vernonia amygdalina, an ethnoveterinary and ethnomedical used green vegetable with multiple bioactivities",
            authors = "Yeap, S. K. et al.",
            year = 2010,
            citation = "Yeap et al. (2010), J. Med. Plants Res., 4(25):2787-2812",
            section = "Safety Warnings & Pregnancy Risk",
            text = "CRITICAL SAFETY WARNING: Experimental pharmacodynamic testing on isolated rat and guinea pig uterine strips has confirmed that aqueous Vernonia amygdalina extracts stimulate rhythmic, concentration-dependent uterine contractions. This pharmacologic activity directly mirrors its traditional use in postpartum placental expulsion. CONSEQUENTLY, BITTER LEAF EXTRACTS ARE CONTRAINDICATED IN PREGNANCY due to the substantial hazard of inducing premature labor or spontaneous abortion. Excessive ingestion can also cause pronounced gastrointestinal emesis and diarrhea.",
            evidenceTier = EvidenceLevel.ANIMAL_RESEARCH,
            keywords = "vernonia amygdalina, safety, contraindication, pregnancy, abortion, uterine contractions, oxytocic, diarrhea, nausea"
        ),

        // Harpagophytum procumbens chunks
        DocumentChunk(
            id = "CHUNK-HARP-01",
            plantId = "PLANT-004",
            plantScientificName = "Harpagophytum procumbens (Burch.) DC. ex Meisn.",
            sourceId = "REF-HARP-01",
            sourceTitle = "Herbal medicine for low back pain: A Cochrane review",
            authors = "Gagnier, J. J., van Tulder, M. W., Berman, B., & Bombardieri, D.",
            year = 2007,
            citation = "Gagnier et al. (2007), Spine, 32(1):82-92",
            section = "Clinical Trials & Cochrane Review",
            text = "A Cochrane Systematic Review evaluating randomized controlled trials established that standardized dry extracts of Harpagophytum procumbens containing 50-100 mg of harpagoside daily produced statistically significant pain reduction in acute exacerbations of chronic low back pain compared to placebo. Secondary randomized trials also demonstrate significant improvement in pain and physical function scores in osteoarthritis of the knee and hip, with analgesic efficacy comparable in certain trials to 12.5 mg rofecoxib, accompanied by a lower incidence of adverse gastrointestinal side effects.",
            evidenceTier = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
            keywords = "harpagophytum procumbens, devil's claw, cochrane review, clinical trials, back pain, arthritis, osteoarthritis, harpagoside"
        ),
        DocumentChunk(
            id = "CHUNK-HARP-02",
            plantId = "PLANT-004",
            plantScientificName = "Harpagophytum procumbens (Burch.) DC. ex Meisn.",
            sourceId = "REF-HARP-02",
            sourceTitle = "Devil's Claw - A review of the ethnobotany, phytochemistry and biological activity of Harpagophytum procumbens",
            authors = "Mncwangi, N. et al.",
            year = 2012,
            citation = "Mncwangi et al. (2012), J. Ethnopharmacol., 143(3):755-778",
            section = "Traditional Ethnobotany & Kalahari Context",
            text = "Indigenous San and Khoi peoples of the Kalahari desert (Botswana, Namibia, South Africa) have traditionally harvested the secondary storage tubers of Harpagophytum (known locally as Sengaparile or Duiwelsklou). The chopped sun-dried tubers were macerated in cold water or boiled as an intensely bitter tonic to treat degenerative joint pains, childbirth pain, gastrointestinal distress, and intermittent desert fevers. Only secondary tubers are collected to preserve the parent taproot and plant survival.",
            evidenceTier = EvidenceLevel.TRADITIONAL_USE_REPORTED,
            keywords = "harpagophytum procumbens, devil's claw, san, khoi, sengaparile, kalahari, botswana, namibia, traditional use, preparation"
        ),
        DocumentChunk(
            id = "CHUNK-HARP-03",
            plantId = "PLANT-004",
            plantScientificName = "Harpagophytum procumbens (Burch.) DC. ex Meisn.",
            sourceId = "REF-HARP-02",
            sourceTitle = "Devil's Claw - A review of the ethnobotany, phytochemistry and biological activity of Harpagophytum procumbens",
            authors = "Mncwangi, N. et al.",
            year = 2012,
            citation = "Mncwangi et al. (2012), J. Ethnopharmacol., 143(3):755-778",
            section = "Safety & Gastrointestinal Contraindications",
            text = "CONTRAINDICATIONS: Devil's Claw iridoids stimulate gustatory bitter receptors and trigger reflexive gastric acid secretion. Consequently, it is strictly contraindicated in individuals with active peptic, gastric, or duodenal ulcers. It is also contraindicated or requires close medical supervision in patients with gallstones due to potential stimulation of bile secretion. Due to potential oxytocic activity, it is not recommended during pregnancy.",
            evidenceTier = EvidenceLevel.ANIMAL_RESEARCH,
            keywords = "harpagophytum procumbens, safety, contraindication, stomach ulcer, gastric acid, gallstones, pregnancy"
        ),

        // Catharanthus roseus chunks
        DocumentChunk(
            id = "CHUNK-CATH-01",
            plantId = "PLANT-005",
            plantScientificName = "Catharanthus roseus (L.) G.Don",
            sourceId = "REF-CATH-02",
            sourceTitle = "The discovery of the vinca alkaloids—chemotherapeutic agents against cancer",
            authors = "Noble, R. L.",
            year = 1990,
            citation = "Noble (1990), Biochem. Cell Biol., 68(12):1344-1351",
            section = "Scientific Discovery & Modern Oncology",
            text = "In the 1950s, Canadian researchers investigating folklore reports of Madagascar Periwinkle leaf tea as a diabetic treatment tested crude extracts in animals. While they found no reliable hypoglycemic effect, the animals developed profound, life-threatening leukopenia (collapse of white blood cells) due to bone marrow destruction. This critical observation prompted the isolation of the dimeric indole alkaloids Vinblastine and Vincristine. Today, purified pharmaceutical vinca alkaloids are indispensable, life-saving chemotherapeutic agents used worldwide to cure pediatric acute lymphoblastic leukemia, Hodgkin lymphoma, and Wilms tumor.",
            evidenceTier = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
            keywords = "catharanthus roseus, madagascar periwinkle, vinca alkaloids, vinblastine, vincristine, cancer, chemotherapy, leukemia, lymphoma, oncology"
        ),
        DocumentChunk(
            id = "CHUNK-CATH-02",
            plantId = "PLANT-005",
            plantScientificName = "Catharanthus roseus (L.) G.Don",
            sourceId = "REF-CATH-01",
            sourceTitle = "Catharanthus roseus: Phytochemical and pharmacological properties",
            authors = "Nejat, N. et al.",
            year = 2015,
            citation = "Nejat et al. (2015), Eur. J. Med. Plants, 5(2):118-128",
            section = "Extreme Toxicity Warning: Do Not Ingest",
            text = "CRITICAL TOXICITY WARNING: CATHARANTHUS ROSEUS MUST NEVER BE CONSUMED AS A HOME REMEDY. The whole plant contains potent cytotoxic alkaloids that poison the cellular mitotic apparatus. Unsupervised home consumption of teas or powders causes severe neurotoxicity, sensory ataxia, hair loss, intractable vomiting, and fatal bone marrow suppression (aplasia). The therapeutic concentration of vinca alkaloids achieved in hospital chemotherapy protocols requires complex chemical extraction and precise microgram-level intravenous dosing under intensive oncologic monitoring.",
            evidenceTier = EvidenceLevel.HUMAN_RESEARCH_AVAILABLE,
            keywords = "catharanthus roseus, toxicity, danger, fatal, bone marrow suppression, poison, do not ingest, neurotoxicity, contraindication"
        )
    )
}
