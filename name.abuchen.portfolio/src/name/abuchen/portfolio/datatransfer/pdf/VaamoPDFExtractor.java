package name.abuchen.portfolio.datatransfer.pdf;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import name.abuchen.portfolio.datatransfer.pdf.PDFParser.Block;
import name.abuchen.portfolio.datatransfer.pdf.PDFParser.DocumentType;
import name.abuchen.portfolio.datatransfer.pdf.PDFParser.Transaction;
import name.abuchen.portfolio.model.BuySellEntry;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.PortfolioTransaction;
import name.abuchen.portfolio.model.Transaction.Unit;
import name.abuchen.portfolio.money.Money;

/**
 * PDF extractor for pfs from Vaamo.
 */
public class VaamoPDFExtractor extends AbstractPDFExtractor
{

    public VaamoPDFExtractor(Client client)
    {
        super(client);

        addBankIdentifier("vaamo"); //$NON-NLS-1$
        addBankIdentifier("Sciuridae Vermögensverwaltungs GmbH"); //$NON-NLS-1$
        addBuyTransaction();
    }

    @SuppressWarnings("nls")
    private void addBuyTransaction()
    {
        DocumentType type = new DocumentType("Splittkauf Betrag"); //$NON-NLS-1$
        addDocumentTyp(type);

        Block block = new Block("Splittkauf *Betrag.*"); //$NON-NLS-1$
        type.addBlock(block);
        
        block.set(new Transaction<BuySellEntry>().subject(() -> {
            BuySellEntry entry = new BuySellEntry();
            entry.setType(PortfolioTransaction.Type.BUY);
            return entry;
        })
                        .section("name","amount","currency","shares","wkn","isin","date") 
                        .match("Splittkauf *Betrag *(?<name>.*?) *(?<amount>[\\d]+,\\d{2}) *(?<currency>\\w{3}).*(?<shares>[\\d]+,\\d{3}).*") 
                        .match("\\d* *(?<wkn>\\w*) */ *(?<isin>\\w*) *(?<date>\\d+\\.\\d+\\.\\d{4}) .*") 
                        .assign((t, v) -> {
                            t.setSecurity(getOrCreateSecurity(v));
                            t.setAmount(amount(v));
                            t.setDate(date(v));
                            t.setCurrencyCode(currency(v));
                            t.setShares(shares(v));
                        })
                        //.section("amount","currency") 
                        //.match("^Ausgabeaufschlag .*(?<amount>[\\d]+,\\d{2}) *(?<currency>\\\\w{3})$") 
                        //.assign((t, v) -> t.getPortfolioTransaction().addUnit(fee(v)))
                        //.section("amount","currency") 
                        //.match("^ETF *Transaktionskosten .* (?<amount>[\\d]+,\\d{2}) *(?<currency>\\\\w{3})$") 
                        //.assign((t, v) -> t.getPortfolioTransaction().addUnit(fee(v)))
                        //.section("amount","currency") 
                        //.match("^Additional *Trading *Costs *(?<amount>[\\d]+,\\d{2}) *(?<currency>\\w{3})$") 
                        //.assign((t, v) -> t.getPortfolioTransaction().addUnit(fee(v)))
                        .wrap(t -> new BuySellEntryItem(t)));
    }

    
    private String currency(Map<String, String> v)
    {
        return asCurrencyCode(v.get("currency"));
    }

    private Unit fee(Map<String, String> v) 
    {
        return new Unit(Unit.Type.FEE, money(v));
    }
    
    private Money money(Map<String, String> v) {
        return Money.of(currency(v), asAmount(v.get("amount"))); //$NON-NLS-1$ //$NON-NLS-2$
    } 

    private LocalDateTime date(Map<String, String> v)
    {
        return asDate(v.get("date")); //$NON-NLS-1$
    }

    private long amount(Map<String, String> v)
    {
        return asAmount(v.get("amount")); //$NON-NLS-1$
    }

    private long shares(Map<String, String> v)
    {
        return asShares(v.get("shares")); //$NON-NLS-1$
    }

    @Override
    public String getLabel()
    {
        return "Vaamo"; //$NON-NLS-1$
    }

    @Override
    public List<Item> extract(List<InputFile> files, List<Exception> errors)
    {
        return super.extract(files, errors);
    }
}
