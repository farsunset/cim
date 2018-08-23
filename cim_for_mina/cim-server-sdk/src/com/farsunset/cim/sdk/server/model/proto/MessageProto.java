/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.sdk.server.model.proto;

public final class MessageProto {
	private MessageProto() {
	}

	public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
	}

	public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
		registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
	}

	public interface ModelOrBuilder extends
			// @@protoc_insertion_point(interface_extends:com.farsunset.cim.sdk.server.model.proto.Model)
			com.google.protobuf.MessageOrBuilder {

		/**
		 * <code>string mid = 1;</code>
		 */
		java.lang.String getMid();

		/**
		 * <code>string mid = 1;</code>
		 */
		com.google.protobuf.ByteString getMidBytes();

		/**
		 * <code>string action = 2;</code>
		 */
		java.lang.String getAction();

		/**
		 * <code>string action = 2;</code>
		 */
		com.google.protobuf.ByteString getActionBytes();

		/**
		 * <code>string content = 3;</code>
		 */
		java.lang.String getContent();

		/**
		 * <code>string content = 3;</code>
		 */
		com.google.protobuf.ByteString getContentBytes();

		/**
		 * <code>string sender = 4;</code>
		 */
		java.lang.String getSender();

		/**
		 * <code>string sender = 4;</code>
		 */
		com.google.protobuf.ByteString getSenderBytes();

		/**
		 * <code>string receiver = 5;</code>
		 */
		java.lang.String getReceiver();

		/**
		 * <code>string receiver = 5;</code>
		 */
		com.google.protobuf.ByteString getReceiverBytes();

		/**
		 * <code>string extra = 6;</code>
		 */
		java.lang.String getExtra();

		/**
		 * <code>string extra = 6;</code>
		 */
		com.google.protobuf.ByteString getExtraBytes();

		/**
		 * <code>string title = 7;</code>
		 */
		java.lang.String getTitle();

		/**
		 * <code>string title = 7;</code>
		 */
		com.google.protobuf.ByteString getTitleBytes();

		/**
		 * <code>string format = 8;</code>
		 */
		java.lang.String getFormat();

		/**
		 * <code>string format = 8;</code>
		 */
		com.google.protobuf.ByteString getFormatBytes();

		/**
		 * <code>int64 timestamp = 9;</code>
		 */
		long getTimestamp();
	}

	/**
	 * Protobuf type {@code com.farsunset.cim.sdk.server.model.proto.Model}
	 */
	public static final class Model extends com.google.protobuf.GeneratedMessageV3 implements
			// @@protoc_insertion_point(message_implements:com.farsunset.cim.sdk.server.model.proto.Model)
			ModelOrBuilder {
		// Use Model.newBuilder() to construct.
		private Model(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
			super(builder);
		}

		private Model() {
			mid_ = "";
			action_ = "";
			content_ = "";
			sender_ = "";
			receiver_ = "";
			extra_ = "";
			title_ = "";
			format_ = "";
			timestamp_ = 0L;
		}

		@java.lang.Override
		public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
			return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
		}

		private Model(com.google.protobuf.CodedInputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			this();
			int mutable_bitField0_ = 0;
			try {
				boolean done = false;
				while (!done) {
					int tag = input.readTag();
					switch (tag) {
					case 0:
						done = true;
						break;
					default: {
						if (!input.skipField(tag)) {
							done = true;
						}
						break;
					}
					case 10: {
						java.lang.String s = input.readStringRequireUtf8();

						mid_ = s;
						break;
					}
					case 18: {
						java.lang.String s = input.readStringRequireUtf8();

						action_ = s;
						break;
					}
					case 26: {
						java.lang.String s = input.readStringRequireUtf8();

						content_ = s;
						break;
					}
					case 34: {
						java.lang.String s = input.readStringRequireUtf8();

						sender_ = s;
						break;
					}
					case 42: {
						java.lang.String s = input.readStringRequireUtf8();

						receiver_ = s;
						break;
					}
					case 50: {
						java.lang.String s = input.readStringRequireUtf8();

						extra_ = s;
						break;
					}
					case 58: {
						java.lang.String s = input.readStringRequireUtf8();

						title_ = s;
						break;
					}
					case 66: {
						java.lang.String s = input.readStringRequireUtf8();

						format_ = s;
						break;
					}
					case 72: {

						timestamp_ = input.readInt64();
						break;
					}
					}
				}
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				throw e.setUnfinishedMessage(this);
			} catch (java.io.IOException e) {
				throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
			} finally {
				makeExtensionsImmutable();
			}
		}

		public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
			return com.farsunset.cim.sdk.server.model.proto.MessageProto.internal_static_com_farsunset_cim_sdk_server_model_proto_Model_descriptor;
		}

		protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
			return com.farsunset.cim.sdk.server.model.proto.MessageProto.internal_static_com_farsunset_cim_sdk_server_model_proto_Model_fieldAccessorTable
					.ensureFieldAccessorsInitialized(com.farsunset.cim.sdk.server.model.proto.MessageProto.Model.class,
							com.farsunset.cim.sdk.server.model.proto.MessageProto.Model.Builder.class);
		}

		public static final int MID_FIELD_NUMBER = 1;
		private volatile java.lang.Object mid_;

		/**
		 * <code>string mid = 1;</code>
		 */
		public java.lang.String getMid() {
			java.lang.Object ref = mid_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				mid_ = s;
				return s;
			}
		}

		/**
		 * <code>string mid = 1;</code>
		 */
		public com.google.protobuf.ByteString getMidBytes() {
			java.lang.Object ref = mid_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				mid_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int ACTION_FIELD_NUMBER = 2;
		private volatile java.lang.Object action_;

		/**
		 * <code>string action = 2;</code>
		 */
		public java.lang.String getAction() {
			java.lang.Object ref = action_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				action_ = s;
				return s;
			}
		}

		/**
		 * <code>string action = 2;</code>
		 */
		public com.google.protobuf.ByteString getActionBytes() {
			java.lang.Object ref = action_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				action_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int CONTENT_FIELD_NUMBER = 3;
		private volatile java.lang.Object content_;

		/**
		 * <code>string content = 3;</code>
		 */
		public java.lang.String getContent() {
			java.lang.Object ref = content_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				content_ = s;
				return s;
			}
		}

		/**
		 * <code>string content = 3;</code>
		 */
		public com.google.protobuf.ByteString getContentBytes() {
			java.lang.Object ref = content_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				content_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int SENDER_FIELD_NUMBER = 4;
		private volatile java.lang.Object sender_;

		/**
		 * <code>string sender = 4;</code>
		 */
		public java.lang.String getSender() {
			java.lang.Object ref = sender_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				sender_ = s;
				return s;
			}
		}

		/**
		 * <code>string sender = 4;</code>
		 */
		public com.google.protobuf.ByteString getSenderBytes() {
			java.lang.Object ref = sender_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				sender_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int RECEIVER_FIELD_NUMBER = 5;
		private volatile java.lang.Object receiver_;

		/**
		 * <code>string receiver = 5;</code>
		 */
		public java.lang.String getReceiver() {
			java.lang.Object ref = receiver_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				receiver_ = s;
				return s;
			}
		}

		/**
		 * <code>string receiver = 5;</code>
		 */
		public com.google.protobuf.ByteString getReceiverBytes() {
			java.lang.Object ref = receiver_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				receiver_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int EXTRA_FIELD_NUMBER = 6;
		private volatile java.lang.Object extra_;

		/**
		 * <code>string extra = 6;</code>
		 */
		public java.lang.String getExtra() {
			java.lang.Object ref = extra_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				extra_ = s;
				return s;
			}
		}

		/**
		 * <code>string extra = 6;</code>
		 */
		public com.google.protobuf.ByteString getExtraBytes() {
			java.lang.Object ref = extra_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				extra_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int TITLE_FIELD_NUMBER = 7;
		private volatile java.lang.Object title_;

		/**
		 * <code>string title = 7;</code>
		 */
		public java.lang.String getTitle() {
			java.lang.Object ref = title_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				title_ = s;
				return s;
			}
		}

		/**
		 * <code>string title = 7;</code>
		 */
		public com.google.protobuf.ByteString getTitleBytes() {
			java.lang.Object ref = title_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				title_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int FORMAT_FIELD_NUMBER = 8;
		private volatile java.lang.Object format_;

		/**
		 * <code>string format = 8;</code>
		 */
		public java.lang.String getFormat() {
			java.lang.Object ref = format_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				format_ = s;
				return s;
			}
		}

		/**
		 * <code>string format = 8;</code>
		 */
		public com.google.protobuf.ByteString getFormatBytes() {
			java.lang.Object ref = format_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				format_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int TIMESTAMP_FIELD_NUMBER = 9;
		private long timestamp_;

		/**
		 * <code>int64 timestamp = 9;</code>
		 */
		public long getTimestamp() {
			return timestamp_;
		}

		private byte memoizedIsInitialized = -1;

		public final boolean isInitialized() {
			byte isInitialized = memoizedIsInitialized;
			if (isInitialized == 1)
				return true;
			if (isInitialized == 0)
				return false;

			memoizedIsInitialized = 1;
			return true;
		}

		public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			if (!getMidBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 1, mid_);
			}
			if (!getActionBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 2, action_);
			}
			if (!getContentBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 3, content_);
			}
			if (!getSenderBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 4, sender_);
			}
			if (!getReceiverBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 5, receiver_);
			}
			if (!getExtraBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 6, extra_);
			}
			if (!getTitleBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 7, title_);
			}
			if (!getFormatBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 8, format_);
			}
			if (timestamp_ != 0L) {
				output.writeInt64(9, timestamp_);
			}
		}

		public int getSerializedSize() {
			int size = memoizedSize;
			if (size != -1)
				return size;

			size = 0;
			if (!getMidBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, mid_);
			}
			if (!getActionBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, action_);
			}
			if (!getContentBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, content_);
			}
			if (!getSenderBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, sender_);
			}
			if (!getReceiverBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, receiver_);
			}
			if (!getExtraBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, extra_);
			}
			if (!getTitleBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, title_);
			}
			if (!getFormatBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(8, format_);
			}
			if (timestamp_ != 0L) {
				size += com.google.protobuf.CodedOutputStream.computeInt64Size(9, timestamp_);
			}
			memoizedSize = size;
			return size;
		}

		private static final long serialVersionUID = 0L;

		@java.lang.Override
		public boolean equals(final java.lang.Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof com.farsunset.cim.sdk.server.model.proto.MessageProto.Model)) {
				return super.equals(obj);
			}
			com.farsunset.cim.sdk.server.model.proto.MessageProto.Model other = (com.farsunset.cim.sdk.server.model.proto.MessageProto.Model) obj;

			boolean result = true;
			result = result && getMid().equals(other.getMid());
			result = result && getAction().equals(other.getAction());
			result = result && getContent().equals(other.getContent());
			result = result && getSender().equals(other.getSender());
			result = result && getReceiver().equals(other.getReceiver());
			result = result && getExtra().equals(other.getExtra());
			result = result && getTitle().equals(other.getTitle());
			result = result && getFormat().equals(other.getFormat());
			result = result && (getTimestamp() == other.getTimestamp());
			return result;
		}

		@java.lang.Override
		public int hashCode() {
			if (memoizedHashCode != 0) {
				return memoizedHashCode;
			}
			int hash = 41;
			hash = (19 * hash) + getDescriptor().hashCode();
			hash = (37 * hash) + MID_FIELD_NUMBER;
			hash = (53 * hash) + getMid().hashCode();
			hash = (37 * hash) + ACTION_FIELD_NUMBER;
			hash = (53 * hash) + getAction().hashCode();
			hash = (37 * hash) + CONTENT_FIELD_NUMBER;
			hash = (53 * hash) + getContent().hashCode();
			hash = (37 * hash) + SENDER_FIELD_NUMBER;
			hash = (53 * hash) + getSender().hashCode();
			hash = (37 * hash) + RECEIVER_FIELD_NUMBER;
			hash = (53 * hash) + getReceiver().hashCode();
			hash = (37 * hash) + EXTRA_FIELD_NUMBER;
			hash = (53 * hash) + getExtra().hashCode();
			hash = (37 * hash) + TITLE_FIELD_NUMBER;
			hash = (53 * hash) + getTitle().hashCode();
			hash = (37 * hash) + FORMAT_FIELD_NUMBER;
			hash = (53 * hash) + getFormat().hashCode();
			hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
			hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getTimestamp());
			hash = (29 * hash) + unknownFields.hashCode();
			memoizedHashCode = hash;
			return hash;
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(
				com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(
				com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(byte[] data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(byte[] data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(java.io.InputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseDelimitedFrom(
				java.io.InputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseDelimitedFrom(
				java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input,
					extensionRegistry);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(
				com.google.protobuf.CodedInputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parseFrom(
				com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
		}

		public Builder newBuilderForType() {
			return newBuilder();
		}

		public static Builder newBuilder() {
			return DEFAULT_INSTANCE.toBuilder();
		}

		public static Builder newBuilder(com.farsunset.cim.sdk.server.model.proto.MessageProto.Model prototype) {
			return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
		}

		public Builder toBuilder() {
			return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
		}

		@java.lang.Override
		protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
			Builder builder = new Builder(parent);
			return builder;
		}

		/**
		 * Protobuf type {@code com.farsunset.cim.sdk.server.model.proto.Model}
		 */
		public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
				// @@protoc_insertion_point(builder_implements:com.farsunset.cim.sdk.server.model.proto.Model)
				com.farsunset.cim.sdk.server.model.proto.MessageProto.ModelOrBuilder {
			public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
				return com.farsunset.cim.sdk.server.model.proto.MessageProto.internal_static_com_farsunset_cim_sdk_server_model_proto_Model_descriptor;
			}

			protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
				return com.farsunset.cim.sdk.server.model.proto.MessageProto.internal_static_com_farsunset_cim_sdk_server_model_proto_Model_fieldAccessorTable
						.ensureFieldAccessorsInitialized(
								com.farsunset.cim.sdk.server.model.proto.MessageProto.Model.class,
								com.farsunset.cim.sdk.server.model.proto.MessageProto.Model.Builder.class);
			}

			// Construct using
			// com.farsunset.cim.sdk.server.model.proto.MessageProto.Model.newBuilder()
			private Builder() {
				maybeForceBuilderInitialization();
			}

			private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
				super(parent);
				maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
				}
			}

			public Builder clear() {
				super.clear();
				mid_ = "";

				action_ = "";

				content_ = "";

				sender_ = "";

				receiver_ = "";

				extra_ = "";

				title_ = "";

				format_ = "";

				timestamp_ = 0L;

				return this;
			}

			public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
				return com.farsunset.cim.sdk.server.model.proto.MessageProto.internal_static_com_farsunset_cim_sdk_server_model_proto_Model_descriptor;
			}

			public com.farsunset.cim.sdk.server.model.proto.MessageProto.Model getDefaultInstanceForType() {
				return com.farsunset.cim.sdk.server.model.proto.MessageProto.Model.getDefaultInstance();
			}

			public com.farsunset.cim.sdk.server.model.proto.MessageProto.Model build() {
				com.farsunset.cim.sdk.server.model.proto.MessageProto.Model result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				}
				return result;
			}

			public com.farsunset.cim.sdk.server.model.proto.MessageProto.Model buildPartial() {
				com.farsunset.cim.sdk.server.model.proto.MessageProto.Model result = new com.farsunset.cim.sdk.server.model.proto.MessageProto.Model(
						this);
				result.mid_ = mid_;
				result.action_ = action_;
				result.content_ = content_;
				result.sender_ = sender_;
				result.receiver_ = receiver_;
				result.extra_ = extra_;
				result.title_ = title_;
				result.format_ = format_;
				result.timestamp_ = timestamp_;
				onBuilt();
				return result;
			}

			public Builder clone() {
				return (Builder) super.clone();
			}

			public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, Object value) {
				return (Builder) super.setField(field, value);
			}

			public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
				return (Builder) super.clearField(field);
			}

			public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
				return (Builder) super.clearOneof(oneof);
			}

			public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index,
					Object value) {
				return (Builder) super.setRepeatedField(field, index, value);
			}

			public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, Object value) {
				return (Builder) super.addRepeatedField(field, value);
			}

			public Builder mergeFrom(com.google.protobuf.Message other) {
				if (other instanceof com.farsunset.cim.sdk.server.model.proto.MessageProto.Model) {
					return mergeFrom((com.farsunset.cim.sdk.server.model.proto.MessageProto.Model) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public Builder mergeFrom(com.farsunset.cim.sdk.server.model.proto.MessageProto.Model other) {
				if (other == com.farsunset.cim.sdk.server.model.proto.MessageProto.Model.getDefaultInstance())
					return this;
				if (!other.getMid().isEmpty()) {
					mid_ = other.mid_;
					onChanged();
				}
				if (!other.getAction().isEmpty()) {
					action_ = other.action_;
					onChanged();
				}
				if (!other.getContent().isEmpty()) {
					content_ = other.content_;
					onChanged();
				}
				if (!other.getSender().isEmpty()) {
					sender_ = other.sender_;
					onChanged();
				}
				if (!other.getReceiver().isEmpty()) {
					receiver_ = other.receiver_;
					onChanged();
				}
				if (!other.getExtra().isEmpty()) {
					extra_ = other.extra_;
					onChanged();
				}
				if (!other.getTitle().isEmpty()) {
					title_ = other.title_;
					onChanged();
				}
				if (!other.getFormat().isEmpty()) {
					format_ = other.format_;
					onChanged();
				}
				if (other.getTimestamp() != 0L) {
					setTimestamp(other.getTimestamp());
				}
				onChanged();
				return this;
			}

			public final boolean isInitialized() {
				return true;
			}

			public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
				com.farsunset.cim.sdk.server.model.proto.MessageProto.Model parsedMessage = null;
				try {
					parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					parsedMessage = (com.farsunset.cim.sdk.server.model.proto.MessageProto.Model) e
							.getUnfinishedMessage();
					throw e.unwrapIOException();
				} finally {
					if (parsedMessage != null) {
						mergeFrom(parsedMessage);
					}
				}
				return this;
			}

			private java.lang.Object mid_ = "";

			/**
			 * <code>string mid = 1;</code>
			 */
			public java.lang.String getMid() {
				java.lang.Object ref = mid_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					mid_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string mid = 1;</code>
			 */
			public com.google.protobuf.ByteString getMidBytes() {
				java.lang.Object ref = mid_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					mid_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string mid = 1;</code>
			 */
			public Builder setMid(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				mid_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string mid = 1;</code>
			 */
			public Builder clearMid() {

				mid_ = getDefaultInstance().getMid();
				onChanged();
				return this;
			}

			/**
			 * <code>string mid = 1;</code>
			 */
			public Builder setMidBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				mid_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object action_ = "";

			/**
			 * <code>string action = 2;</code>
			 */
			public java.lang.String getAction() {
				java.lang.Object ref = action_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					action_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string action = 2;</code>
			 */
			public com.google.protobuf.ByteString getActionBytes() {
				java.lang.Object ref = action_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					action_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string action = 2;</code>
			 */
			public Builder setAction(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				action_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string action = 2;</code>
			 */
			public Builder clearAction() {

				action_ = getDefaultInstance().getAction();
				onChanged();
				return this;
			}

			/**
			 * <code>string action = 2;</code>
			 */
			public Builder setActionBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				action_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object content_ = "";

			/**
			 * <code>string content = 3;</code>
			 */
			public java.lang.String getContent() {
				java.lang.Object ref = content_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					content_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string content = 3;</code>
			 */
			public com.google.protobuf.ByteString getContentBytes() {
				java.lang.Object ref = content_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					content_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string content = 3;</code>
			 */
			public Builder setContent(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				content_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string content = 3;</code>
			 */
			public Builder clearContent() {

				content_ = getDefaultInstance().getContent();
				onChanged();
				return this;
			}

			/**
			 * <code>string content = 3;</code>
			 */
			public Builder setContentBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				content_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object sender_ = "";

			/**
			 * <code>string sender = 4;</code>
			 */
			public java.lang.String getSender() {
				java.lang.Object ref = sender_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					sender_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string sender = 4;</code>
			 */
			public com.google.protobuf.ByteString getSenderBytes() {
				java.lang.Object ref = sender_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					sender_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string sender = 4;</code>
			 */
			public Builder setSender(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				sender_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string sender = 4;</code>
			 */
			public Builder clearSender() {

				sender_ = getDefaultInstance().getSender();
				onChanged();
				return this;
			}

			/**
			 * <code>string sender = 4;</code>
			 */
			public Builder setSenderBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				sender_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object receiver_ = "";

			/**
			 * <code>string receiver = 5;</code>
			 */
			public java.lang.String getReceiver() {
				java.lang.Object ref = receiver_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					receiver_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string receiver = 5;</code>
			 */
			public com.google.protobuf.ByteString getReceiverBytes() {
				java.lang.Object ref = receiver_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					receiver_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string receiver = 5;</code>
			 */
			public Builder setReceiver(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				receiver_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string receiver = 5;</code>
			 */
			public Builder clearReceiver() {

				receiver_ = getDefaultInstance().getReceiver();
				onChanged();
				return this;
			}

			/**
			 * <code>string receiver = 5;</code>
			 */
			public Builder setReceiverBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				receiver_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object extra_ = "";

			/**
			 * <code>string extra = 6;</code>
			 */
			public java.lang.String getExtra() {
				java.lang.Object ref = extra_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					extra_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string extra = 6;</code>
			 */
			public com.google.protobuf.ByteString getExtraBytes() {
				java.lang.Object ref = extra_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					extra_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string extra = 6;</code>
			 */
			public Builder setExtra(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				extra_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string extra = 6;</code>
			 */
			public Builder clearExtra() {

				extra_ = getDefaultInstance().getExtra();
				onChanged();
				return this;
			}

			/**
			 * <code>string extra = 6;</code>
			 */
			public Builder setExtraBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				extra_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object title_ = "";

			/**
			 * <code>string title = 7;</code>
			 */
			public java.lang.String getTitle() {
				java.lang.Object ref = title_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					title_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string title = 7;</code>
			 */
			public com.google.protobuf.ByteString getTitleBytes() {
				java.lang.Object ref = title_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					title_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string title = 7;</code>
			 */
			public Builder setTitle(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				title_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string title = 7;</code>
			 */
			public Builder clearTitle() {

				title_ = getDefaultInstance().getTitle();
				onChanged();
				return this;
			}

			/**
			 * <code>string title = 7;</code>
			 */
			public Builder setTitleBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				title_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object format_ = "";

			/**
			 * <code>string format = 8;</code>
			 */
			public java.lang.String getFormat() {
				java.lang.Object ref = format_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					format_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string format = 8;</code>
			 */
			public com.google.protobuf.ByteString getFormatBytes() {
				java.lang.Object ref = format_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					format_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string format = 8;</code>
			 */
			public Builder setFormat(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				format_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string format = 8;</code>
			 */
			public Builder clearFormat() {

				format_ = getDefaultInstance().getFormat();
				onChanged();
				return this;
			}

			/**
			 * <code>string format = 8;</code>
			 */
			public Builder setFormatBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				format_ = value;
				onChanged();
				return this;
			}

			private long timestamp_;

			/**
			 * <code>int64 timestamp = 9;</code>
			 */
			public long getTimestamp() {
				return timestamp_;
			}

			/**
			 * <code>int64 timestamp = 9;</code>
			 */
			public Builder setTimestamp(long value) {

				timestamp_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>int64 timestamp = 9;</code>
			 */
			public Builder clearTimestamp() {

				timestamp_ = 0L;
				onChanged();
				return this;
			}

			public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
				return this;
			}

			public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
				return this;
			}

			// @@protoc_insertion_point(builder_scope:com.farsunset.cim.sdk.server.model.proto.Model)
		}

		// @@protoc_insertion_point(class_scope:com.farsunset.cim.sdk.server.model.proto.Model)
		private static final com.farsunset.cim.sdk.server.model.proto.MessageProto.Model DEFAULT_INSTANCE;
		static {
			DEFAULT_INSTANCE = new com.farsunset.cim.sdk.server.model.proto.MessageProto.Model();
		}

		public static com.farsunset.cim.sdk.server.model.proto.MessageProto.Model getDefaultInstance() {
			return DEFAULT_INSTANCE;
		}

		private static final com.google.protobuf.Parser<Model> PARSER = new com.google.protobuf.AbstractParser<Model>() {
			public Model parsePartialFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry)
					throws com.google.protobuf.InvalidProtocolBufferException {
				return new Model(input, extensionRegistry);
			}
		};

		public static com.google.protobuf.Parser<Model> parser() {
			return PARSER;
		}

		@java.lang.Override
		public com.google.protobuf.Parser<Model> getParserForType() {
			return PARSER;
		}

		public com.farsunset.cim.sdk.server.model.proto.MessageProto.Model getDefaultInstanceForType() {
			return DEFAULT_INSTANCE;
		}

	}

	private static final com.google.protobuf.Descriptors.Descriptor internal_static_com_farsunset_cim_sdk_server_model_proto_Model_descriptor;
	private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_com_farsunset_cim_sdk_server_model_proto_Model_fieldAccessorTable;

	public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
		return descriptor;
	}

	private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
	static {
		java.lang.String[] descriptorData = { "\n\rMessage.proto\022(com.farsunset.cim.sdk.s"
				+ "erver.model.proto\"\230\001\n\005Model\022\013\n\003mid\030\001 \001(\t"
				+ "\022\016\n\006action\030\002 \001(\t\022\017\n\007content\030\003 \001(\t\022\016\n\006sen"
				+ "der\030\004 \001(\t\022\020\n\010receiver\030\005 \001(\t\022\r\n\005extra\030\006 \001"
				+ "(\t\022\r\n\005title\030\007 \001(\t\022\016\n\006format\030\010 \001(\t\022\021\n\ttim"
				+ "estamp\030\t \001(\003B\016B\014MessageProtob\006proto3" };
		com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
			public com.google.protobuf.ExtensionRegistry assignDescriptors(
					com.google.protobuf.Descriptors.FileDescriptor root) {
				descriptor = root;
				return null;
			}
		};
		com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
				new com.google.protobuf.Descriptors.FileDescriptor[] {}, assigner);
		internal_static_com_farsunset_cim_sdk_server_model_proto_Model_descriptor = getDescriptor().getMessageTypes()
				.get(0);
		internal_static_com_farsunset_cim_sdk_server_model_proto_Model_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
				internal_static_com_farsunset_cim_sdk_server_model_proto_Model_descriptor, new java.lang.String[] {
						"Mid", "Action", "Content", "Sender", "Receiver", "Extra", "Title", "Format", "Timestamp", });
	}

	// @@protoc_insertion_point(outer_class_scope)
}
