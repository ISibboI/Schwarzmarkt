package de.isibboi.schwarzmarkt;

import java.util.LinkedList;
import java.util.List;

import android.R.anim;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AutoCompleteTextView;

public class ChooseCardsActivity extends Activity {
	private final List<Card> cards = new LinkedList<>();
	private int exclusionProgress = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_cards);

		for (Parcelable edition : getIntent().getExtras()
				.getParcelableArrayList("usedEditions")) {
			cards.addAll(((Edition) edition).getCards());
		}

		refreshSelector();

		findViewById(R.id.add_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CharSequence name = ((TextView) findViewById(R.id.card_selector))
						.getText();

				Log.v(getClass().getName(), "Searching for: " + name);

				for (final Card card : cards) {
					if (charSequenceEquals(card.getName(), name)) {
						Log.v(getClass().getName(),
								"Found card: " + card.getName());

						((TextView) findViewById(R.id.card_selector))
								.setText("");
						cards.remove(card);
						refreshSelector();

						final TextView cardNameView = new TextView(
								getApplicationContext());
						cardNameView.setText(card.getName());
						cardNameView.setTextAppearance(getApplicationContext(),
								R.style.TextAppearance_AppCompat_Large);
						cardNameView.setTextColor(getResources().getColor(R.color.black));
						cardNameView.setGravity(Gravity.CENTER);

						cardNameView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								cards.add(card);
								((LinearLayout) findViewById(R.id.exclusion_list))
										.removeView(cardNameView);
								changeExclusionProgress(false);
								refreshSelector();
							}
						});

						LinearLayout exclusionList = (LinearLayout) findViewById(R.id.exclusion_list);
						exclusionList.addView(cardNameView);
						changeExclusionProgress(true);

						return;
					}
				}
			}

			private boolean charSequenceEquals(CharSequence a, CharSequence b) {
				if (a.length() != b.length()) {
					return false;
				}

				for (int i = 0; i < a.length(); i++) {
					if (a.charAt(i) != b.charAt(i)) {
						return false;
					}
				}

				return true;
			}
		});

		Log.v(getClass().getName(), "Creation finished.");
	}

	private void refreshSelector() {
		String[] names = new String[cards.size()];

		int i = 0;
		for (Card card : cards) {
			names[i++] = card.getName();
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_dropdown_item_1line, names);
		AutoCompleteTextView cardSelector = ((AutoCompleteTextView) findViewById(R.id.card_selector));
		cardSelector.setAdapter(adapter);
		cardSelector.setThreshold(0);
	}

	public void changeExclusionProgress(boolean up) {
		if (up) {
			exclusionProgress++;
		} else {
			exclusionProgress--;
		}

		if (exclusionProgress >= 10) {
			Log.v(getClass().getName(), "Exclusion progress is: 10");
			((ProgressBar) findViewById(R.id.exclusion_progress_bar))
					.setProgress(10);
		} else {
			Log.v(getClass().getName(), "Exclusion progress is: "
					+ exclusionProgress);
			((ProgressBar) findViewById(R.id.exclusion_progress_bar))
					.setProgress(exclusionProgress);
		}
	}
}
