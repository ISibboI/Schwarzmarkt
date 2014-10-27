package de.isibboi.schwarzmarkt;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {
	public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
		@Override
		public Card createFromParcel(Parcel source) {
			return new Card(source.readString(), source.readString());
		}

		@Override
		public Card[] newArray(int size) {
			return new Card[size];
		}
	};
	
	private final String name;
	private final String description;
	
	public Card(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(description);
	}
}
