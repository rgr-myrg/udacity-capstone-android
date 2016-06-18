package net.usrlib.pocketbuddha.mvp;

import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by rgr-myrg on 6/15/16.
 */

public interface MvpView {
	void onTransactionProgress(MvpPresenter.TransactionType type);
	void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data);
	void onTransactionError(MvpPresenter.TransactionType type);
	void onTransactionCursorReady(Cursor cursor);
	void requestTransaction(Bundle data);
}