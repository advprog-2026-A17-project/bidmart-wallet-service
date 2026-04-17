package id.ac.ui.cs.advprog.bidmartwalletservice.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}
