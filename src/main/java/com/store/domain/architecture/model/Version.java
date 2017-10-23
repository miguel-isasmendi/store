package com.store.domain.architecture.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import autovalue.shaded.org.apache.commons.lang.StringUtils;
import lombok.Getter;

@Getter
public class Version implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)?");

	private String fullVersion;
	private int major;
	private int minor;
	private int patch;

	public static boolean isValidVersion(String fullVersion) {
		Preconditions.checkArgument(StringUtils.isNotBlank(fullVersion), "version  is null, blank or empty");

		Matcher matcher = VERSION_PATTERN.matcher(fullVersion);
		return matcher.matches();
	}

	public boolean isGreaterThan(Version otherVersion) {
		boolean isGreater = getMajor() > otherVersion.getMajor();

		if (!isGreater && getMajor() == otherVersion.getMajor()) {

			isGreater = getMinor() > otherVersion.getMinor();

			if (!isGreater && getMinor() == otherVersion.getMinor()) {
				isGreater = getPatch() > otherVersion.getPatch();
			}
		}

		return isGreater;
	}
	
	public boolean isLesserThan(Version otherVersion) {
		boolean isLesser = mayorIsLesserThan(otherVersion);

		if (!isLesser && getMajor() == otherVersion.getMajor()) {

			isLesser = getMinor() < otherVersion.getMinor();

			if (!isLesser && getMinor() == otherVersion.getMinor()) {
				isLesser = getPatch() < otherVersion.getPatch();
			}
		}

		return isLesser;
	}
	
	public boolean mayorIsLesserThan(Version otherVersion) {
		return getMajor() < otherVersion.getMajor();
	}

	public static VersionBuilder builder() {
		return new VersionBuilder();
	}

	public static class VersionBuilder {

		private String fullVersion;

		public Version build() {
			Version resultVersion = new Version();

			Preconditions.checkArgument(StringUtils.isNotBlank(fullVersion), "version  is null, blank or empty");

			Matcher matcher = VERSION_PATTERN.matcher(fullVersion);
			if (matcher.matches()) {
				resultVersion.major = Integer.valueOf(matcher.group(1));
				resultVersion.minor = Integer.valueOf(matcher.group(2));
				resultVersion.patch = Integer.valueOf(matcher.group(3));

				resultVersion.fullVersion = fullVersion;
			} else {
				throw new IllegalArgumentException("Invalid version:[" + fullVersion + "]");
			}

			return resultVersion;
		}

		public VersionBuilder setVersion(String version) {
			this.fullVersion = version;
			return this;
		}

	}

}
