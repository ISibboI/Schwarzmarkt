package de.isibboi.schwarzmarkt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Edition implements Parcelable {
	public static final Parcelable.Creator<Edition> CREATOR = new Parcelable.Creator<Edition>() {
		@Override
		public Edition createFromParcel(Parcel source) {
			String name = source.readString();
			ArrayList<Card> cards = new ArrayList<>();
			source.readTypedList(cards, Card.CREATOR);
			boolean forceSelection = source.readByte() == 1;
			
			return new Edition(name, cards, forceSelection);
		}

		@Override
		public Edition[] newArray(int size) {
			return new Edition[size];
		}
	};
	
	private final String name;
	private final List<Card> cards = new ArrayList<>();
	private final boolean forceSelection;

	public Edition(String name, Collection<Card> cards, boolean forceSelection) {
		this.name = name;
		this.cards.addAll(cards);
		this.forceSelection = forceSelection;
	}

	public String getName() {
		return name;
	}

	public List<Card> getCards() {
		return cards;
	}

	public boolean isForceSelection() {
		return forceSelection;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Edition) {
			Edition e = (Edition) o;
			
			return e.name.equals(name);
		} else {
			return false;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeTypedList(cards);
		dest.writeByte((byte) (forceSelection ? 1 : 0));
	}
}