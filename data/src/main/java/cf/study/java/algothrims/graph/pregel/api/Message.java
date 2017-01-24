package cf.study.java.algothrims.graph.pregel.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Created by fan on 2017/1/17.
 */
public class Message<V extends Serializable & Comparable<V>> extends BaseObj {
	private long destVertexID;

	private long srcVertexID;

	private long superStepID;

	private V value;

	public Message() {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Message))
			return false;
		Message<?> message = (Message<?>) o;
		return getSrcVertexID() == message.getSrcVertexID() && getDestVertexID() == message.getDestVertexID() && getSuperStepID() == message.getSuperStepID() && Objects.equals(getValue(), message.getValue());
	}

	public long getDestVertexID() {
		return destVertexID;
	}

	public long getSrcVertexID() {
		return srcVertexID;
	}

	public long getSuperStepID() {
		return superStepID;
	}

	public V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getValue(), getSrcVertexID(), getDestVertexID(), getSuperStepID());
	}

	public void setDestVertexID(long destVertexID) {
		this.destVertexID = destVertexID;
	}

	public void setSrcVertexID(long srcVertexID) {
		this.srcVertexID = srcVertexID;
	}

	public void setSuperStepID(long superStepID) {
		this.superStepID = superStepID;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("superStepID", getSuperStepID());
		map.put("srcVertexID", getSrcVertexID());
		map.put("destVertexID", getDestVertexID());
		map.put("value", getValue());
		return map;
	}
}
