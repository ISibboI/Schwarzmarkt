package de.isibboi.schwarzmarkt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChooseEditionsActivity extends Activity {
	private static final String EDITIONS_PATH = "editions";
	private static final String USED_EDITIONS_PREFERENCES = "used_editions";

	private final List<Edition> editions = new LinkedList<>();
	private final ArrayList<Edition> usedEditions = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_editions);

		loadEditions();

		SharedPreferences settings = getSharedPreferences(
				USED_EDITIONS_PREFERENCES, 0);

		for (final Edition edition : editions) {
			CheckBox checkBox = new CheckBox(getApplicationContext());
			checkBox.setText(edition.getName());

			if (edition.isForceSelection()) {
				usedEditions.add(edition);
				checkBox.setChecked(true);
				checkBox.setEnabled(false);
			} else if (settings.getBoolean(edition.getName(), false)) {
				usedEditions.add(edition);
				checkBox.setChecked(true);
			}

			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						usedEditions.add(edition);
					} else {
						usedEditions.remove(edition);
					}
				}
			});

			((LinearLayout) findViewById(R.id.edition_list)).addView(checkBox);
			Log.v(getClass().getName(), "Added edition: " + edition.getName());
		}

		findViewById(R.id.start_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ChooseEditionsActivity.this,
								ChooseCardsActivity.class);
						Bundle b = new Bundle();
						b.putParcelableArrayList("usedEditions", usedEditions);
						intent.putExtras(b);
						startActivity(intent);
						finish();
					}
				});

		Log.v(getClass().getName(), "Creation finished.");
	}

	private void loadEditions() {
		try {
			String[] editions = getAssets().list(EDITIONS_PATH);

			Log.v(getClass().getName(), "Found " + editions.length
					+ " editions.");

			for (String edition : editions) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						getAssets().open(EDITIONS_PATH + "/" + edition)));

				Collection<Card> cards = new LinkedList<>();
				String name = null;
				String editionName = edition.substring(0, edition.length() - 4);
				boolean forceSelection = false;
				String description = "";
				String line;

				while ((line = in.readLine()) != null) {
					line = line.trim();

					if (line.startsWith("!")) {
						line = line.substring(1);

						if (line.equals("forceSelection")) {
							forceSelection = true;
						}
					} else if (line.startsWith(":")) {
						String[] argument = line.substring(1).split("=");

						if (argument[0].equals("name")) {
							editionName = argument[1];
						}
					} else if (line.startsWith("#")) {
						if (name != null) {
							cards.add(new Card(name, description));
							name = null;
							description = "";
						}

						name = line.substring(1);
					} else {
						description += "\n" + line;
					}
				}

				if (name != null) {
					cards.add(new Card(name, description));
				}

				this.editions.add(new Edition(editionName, cards,
						forceSelection));
				in.close();
			}

			Log.i(getClass().getName(), "Editions loaded.");
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Could not load assets!",
					Toast.LENGTH_LONG).show();
			Log.e(getClass().getName(), "Could not load editions!", e);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		SharedPreferences settings = getSharedPreferences(
				USED_EDITIONS_PREFERENCES, 0);
		SharedPreferences.Editor editor = settings.edit();

		for (Edition edition : editions) {
			editor.putBoolean(edition.getName(),
					usedEditions.contains(edition));
		}

		editor.commit();
	}
}