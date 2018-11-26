package name.abuchen.portfolio.datatransfer.pdf.vaamo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import name.abuchen.portfolio.datatransfer.Extractor.BuySellEntryItem;
import name.abuchen.portfolio.datatransfer.Extractor;
import name.abuchen.portfolio.datatransfer.Extractor.Item;
import name.abuchen.portfolio.datatransfer.Extractor.SecurityItem;
import name.abuchen.portfolio.datatransfer.pdf.PDFInputFile;
import name.abuchen.portfolio.datatransfer.pdf.VaamoPDFExtractor;
import name.abuchen.portfolio.model.AccountTransaction;
import name.abuchen.portfolio.model.BuySellEntry;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.money.CurrencyUnit;
import name.abuchen.portfolio.money.Money;
import name.abuchen.portfolio.money.Values;

public class VaamoBankPDFExtractorTest
{
    
    public static void main(String [ ] args) {
        System.out.println("hallo");
        
        String strToMatch = "Splittkauf Betrag iShares Pfandbriefe (DE) 6,70 EUR 104,6000 EUR 0,064";
                        
        Pattern pattern = Pattern.compile("Splittkauf *Betrag *(?<name>[^ ]*) .*");
        
        Matcher m = pattern.matcher(strToMatch);
        
        System.out.println("This str matches: " + m.matches());
       
        
        
    }
    
    @Test
    //test input of wrong file
    public void testEmptyFile() throws IOException {
        Extractor extr = newExtractor();
        
        List<Exception> errors = new ArrayList<>();
        List<Item> results = extr.extract(PDFInputFile.createTestCase("foo.pdf", "foo bar"), errors);
        
        assertThat(results, empty());
        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), instanceOf(UnsupportedOperationException.class));
    }
    
    
    @Test
    public void testFondsabrechnung1() throws IOException {
        Extractor extr = newExtractor();
        List<Exception> errors = new ArrayList<>();

        List<Item> results = extr.extract(PDFInputFile.loadTestCase(getClass(), "Fondsabrechnung1.txt"), errors);
        assertThat("Expected no errors during parsing.",errors, empty());
        
        // get security
        Optional<Item> item = results.stream().filter(i -> i instanceof SecurityItem).findFirst();
        assertThat(item.isPresent(), is(true));
        Security security = ((SecurityItem) item.get()).getSecurity();
        
        // assert security
        //Fondsname
        assertThat(security.getName(), is("iShares Pfandbriefe (DE)"));
        
        //WKN 263526
        assertThat(security.getWkn(), is("263526"));
        //ISIN DE0002635265
        assertThat(security.getIsin(), is("DE0002635265"));
        assertThat(security.getCurrencyCode(), is(CurrencyUnit.EUR));
        //0,064
      
        
      
        // get transaction
        item = results.stream().filter(i -> i instanceof BuySellEntryItem).findFirst();
        assertThat(item.isPresent(), is(true));
        assertThat(item.get().getSubject(), instanceOf(BuySellEntry.class));
        BuySellEntry entry = (BuySellEntry) item.get().getSubject();
        AccountTransaction transaction = entry.getAccountTransaction();
        assertNotNull(transaction);
        
        // assert transaction
        //Splittkauf Betrag
        
        assertThat(transaction.getType(), is(AccountTransaction.Type.BUY));
        //Betrag 6,70 EUR
        assertThat(transaction.getMonetaryAmount(), is(Money.of(CurrencyUnit.EUR, 6_70)));
        //Anteile 0,064
        assertThat(transaction.getShares(), is(Values.Share.factorize(0.064)));
        //Preisdatum 16.07.2018
        assertThat(transaction.getDateTime(), is(LocalDateTime.parse("2018-07-16")));
        //Additional Trading Costs 0,02
        //assertThat(transaction.getUnitSum(Unit.Type.FEE), is(Money.of(CurrencyUnit.EUR, Values.Amount.factorize(0.02))));
    }
    
    
   
    
    
    private Extractor newExtractor() {
        return new VaamoPDFExtractor(new Client());
    }
}
